package org.example.service.falha;

import org.example.model.Falha;
import org.example.repositorio.EquipamentoRepositorio;
import org.example.repositorio.FalhaRepositorio;

import java.sql.SQLException;
import java.util.List;

public class FalhaServiceImpl implements FalhaService{
    EquipamentoRepositorio equipamentoRepositorio = new EquipamentoRepositorio();

    FalhaRepositorio falhaRepositorio = new FalhaRepositorio();

    @Override
    public Falha registrarNovaFalha(Falha falha) throws SQLException {
        if(!equipamentoRepositorio.equipamentoExiste(falha.getEquipamentoId())){
            throw new IllegalArgumentException("Equipamento não encontrado!");
        }

        falha.setStatus("ABERTA");

        falha = falhaRepositorio.criarFalha(falha);

        if(falha.getCriticidade() == "CRITICA"){
            equipamentoRepositorio.atualizarStatus(falha.getEquipamentoId(),"EM_MANUTENCAO");
        }

        if(falha.getId() == null){
            throw new RuntimeException("Erro ao salvar falha!");
        }

        return falha;
    }

    @Override
    public List<Falha> buscarFalhasCriticasAbertas() throws SQLException {
        return falhaRepositorio.buscarFalhasCriticasAbertas();
    }
}
