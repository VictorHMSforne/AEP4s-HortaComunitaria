package br.edu.unicesumar;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class CadastroService {
    private final EntityManager entityManager;

    public CadastroService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T extends Registro> List<T> listar(Class<T> tipo) {
        return entityManager.createQuery("from " + tipo.getSimpleName() + " r order by r.id", tipo)
                .getResultList();
    }

    public <T extends Registro> T buscar(Class<T> tipo, long id) {
        return exigir(entityManager.find(tipo, id), "Registro não encontrado.", 404);
    }

    public List<Producao> producoes(long hortaId) {
        return buscar(Horta.class, hortaId).getProducoes();
    }

    @Transactional
    public <T extends Registro> T criar(T registro) {
        validar(registro);
        entityManager.persist(registro);
        entityManager.flush();
        return registro;
    }

    @Transactional
    public <T extends Registro> T atualizar(Class<T> tipo, long id, T registro) {
        T atual = travar(tipo, id, "Registro não encontrado.", 404);
        registro.atribuirId(id);
        validar(registro);
        if (atual instanceof Horta horta && registro instanceof Horta dados) {
            horta.atualizarDados(dados);
            entityManager.flush();
            return atual;
        }
        T atualizado = entityManager.merge(registro);
        entityManager.flush();
        return atualizado;
    }

    @Transactional
    public <T extends Registro> void excluir(Class<T> tipo, long id) {
        entityManager.remove(travar(tipo, id, "Registro não encontrado.", 404));
        entityManager.flush();
    }

    private void validar(Registro registro) {
        switch (registro) {
            case Horta horta -> validarHorta(horta);
            case Terreno terreno -> validarTerreno(terreno);
            case GestorHorta gestor -> validarGestor(gestor);
            case Familia familia -> validarFamilia(familia);
            case ProducaoHorta colheita -> validarColheita(colheita);
            case Distribuicao distribuicao -> validarDistribuicao(distribuicao);
            default -> { }
        }
    }

    private void validarHorta(Horta horta) {
        Terreno terreno = travar(Terreno.class, horta.getTerrenoId(), "Terreno não encontrado.", 409);
        GestorHorta gestor = travar(GestorHorta.class, horta.getGestorId(), "Gestor não encontrado.", 409);
        if (!terreno.getPrefeituraId().equals(gestor.getPrefeituraId())) {
            throw conflito("O gestor deve estar contratado pela prefeitura do terreno.");
        }
        if (!apto(terreno)) {
            throw conflito("A horta precisa de um terreno disponível e legalmente apto.");
        }
    }

    private void validarTerreno(Terreno terreno) {
        if (terreno.getId() == null) {
            return;
        }
        boolean comHortaIncompativel = existe("""
                select 1 from Horta h, GestorHorta g
                where h.terrenoId = :id and g.id = h.gestorId
                and (g.prefeituraId <> :destino or :impedido = true)
                """, terreno.getId(), terreno.getPrefeituraId(), !apto(terreno));
        if (comHortaIncompativel) {
            throw conflito("O terreno está vinculado a uma horta. Revise o vínculo antes de mudar a situação ou a prefeitura.");
        }
    }

    private void validarGestor(GestorHorta gestor) {
        if (gestor.getId() != null && existe("""
                select 1 from Horta h, Terreno t
                where h.gestorId = :id and t.id = h.terrenoId and t.prefeituraId <> :destino
                """, gestor.getId(), gestor.getPrefeituraId(), null)) {
            throw conflito("O gestor é responsável por uma horta da prefeitura atual.");
        }
    }

    private void validarFamilia(Familia familia) {
        if (familia.getId() != null && existe("""
                select 1 from Distribuicao d, ProducaoHorta p
                where d.familiaId = :id and p.id = d.colheitaId and p.hortaId <> :destino
                """, familia.getId(), familia.getHortaId(), null)) {
            throw conflito("A família tem entregas registradas na horta atual.");
        }
    }

    private void validarColheita(ProducaoHorta colheita) {
        if (colheita.getId() == null) {
            return;
        }
        boolean entregaIncompativel = !entityManager.createQuery("""
                select d.id from Distribuicao d, Familia f
                where d.colheitaId = :id and f.id = d.familiaId
                  and (f.hortaId <> :horta or d.dataEntrega < :colheita)
                """, Long.class)
                .setParameter("id", colheita.getId())
                .setParameter("horta", colheita.getHortaId())
                .setParameter("colheita", colheita.getDataColheita())
                .setMaxResults(1).getResultList().isEmpty();
        if (entregaIncompativel || totalDistribuido(colheita.getId(), null).compareTo(colheita.calcularRendimento()) > 0) {
            throw conflito("A alteração da colheita é incompatível com entregas já registradas.");
        }
    }

    private void validarDistribuicao(Distribuicao distribuicao) {
        Familia familia = travar(Familia.class, distribuicao.getFamiliaId(), "Família não encontrada.", 409);
        ProducaoHorta colheita = travar(ProducaoHorta.class, distribuicao.getColheitaId(), "Colheita não encontrada.", 409);
        if (!Objects.equals(familia.getHortaId(), colheita.getHortaId())) {
            throw conflito("A família e a colheita devem pertencer à mesma horta.");
        }
        if (distribuicao.getDataEntrega().isBefore(colheita.getDataColheita())) {
            throw new ErroCadastro(400, "A entrega deve ocorrer na data da colheita ou depois.");
        }
        BigDecimal total = totalDistribuido(colheita.getId(), distribuicao.getId()).add(distribuicao.getQuantidade());
        if (total.compareTo(colheita.calcularRendimento()) > 0) {
            throw conflito("A quantidade excede o saldo disponível da colheita.");
        }
    }

    private BigDecimal totalDistribuido(long colheitaId, Long ignorarId) {
        String consulta = "select sum(d.quantidade) from Distribuicao d where d.colheitaId = :colheita";
        if (ignorarId != null) {
            consulta += " and d.id <> :ignorar";
        }
        var query = entityManager.createQuery(consulta, BigDecimal.class).setParameter("colheita", colheitaId);
        if (ignorarId != null) {
            query.setParameter("ignorar", ignorarId);
        }
        return Objects.requireNonNullElse(query.getSingleResult(), BigDecimal.ZERO);
    }

    private boolean existe(String hql, long id, long destino, Boolean impedido) {
        var consulta = entityManager.createQuery(hql, Integer.class)
                .setParameter("id", id).setParameter("destino", destino);
        if (impedido != null) {
            consulta.setParameter("impedido", impedido);
        }
        return !consulta.setMaxResults(1).getResultList().isEmpty();
    }

    private boolean apto(Terreno terreno) {
        return terreno.getStatus() == Terreno.Status.DISPONIVEL
                && terreno.getSituacaoLegal() == Terreno.SituacaoLegal.APTO;
    }

    private <T extends Registro> T travar(Class<T> tipo, long id, String mensagem, int status) {
        return exigir(entityManager.find(tipo, id, LockModeType.PESSIMISTIC_WRITE), mensagem, status);
    }

    private <T> T exigir(T registro, String mensagem, int status) {
        if (registro == null) {
            throw new ErroCadastro(status, mensagem);
        }
        return registro;
    }

    private ErroCadastro conflito(String mensagem) {
        return new ErroCadastro(409, mensagem);
    }
}
