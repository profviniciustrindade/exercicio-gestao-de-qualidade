package org.example.service.relatorioservice;

import org.example.dto.EquipamentoContagemFalhasDTO;
import org.example.dto.FalhaDetalhadaDTO;
import org.example.dto.RelatorioParadaDTO;
import org.example.model.Equipamento;
import org.example.model.Falha;
import org.example.repositorio.AcaoCorretivaRepositorio;
import org.example.repositorio.EquipamentoRepositorio;
import org.example.repositorio.FalhaRepositorio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RelatorioServiceImpl implements RelatorioService{
    private FalhaRepositorio falhaRepositorio = new FalhaRepositorio();

    private EquipamentoRepositorio equipamentoRepositorio = new EquipamentoRepositorio();

    private AcaoCorretivaRepositorio acaoCorretivaRepositorio = new AcaoCorretivaRepositorio();

    @Override
    public List<RelatorioParadaDTO> gerarRelatorioTempoParada() throws SQLException {
        return falhaRepositorio.buscarTodosRelatoriosParada();
    }

    @Override
    public List<Equipamento> buscarEquipamentosSemFalhasPorPeriodo(LocalDate dataInicio, LocalDate datafim) throws SQLException {
        return equipamentoRepositorio.buscarEquipamentosSemFalha(dataInicio,datafim);
    }

    @Override
    public Optional<FalhaDetalhadaDTO> buscarDetalhesCompletosFalha(long falhaId) throws SQLException {
        Falha falha = falhaRepositorio.buscarFalhaPorId(falhaId);

        if(falha == null){
            throw new RuntimeException();
        }

        Equipamento equipamento = equipamentoRepositorio.buscarEquipamentoPorId(falha.getEquipamentoId());

        if(equipamento == null){
            throw new RuntimeException();
        }

        List<String> acoes = acaoCorretivaRepositorio.buscarAcaoCorretivaPorIdFalha(falhaId);
        return Optional.of(new FalhaDetalhadaDTO(falha, equipamento, acoes));
    }

    @Override
    public List<EquipamentoContagemFalhasDTO> gerarRelatorioManutencaoPreventiva(int contagemMinimaFalhas) throws SQLException {
        if(contagemMinimaFalhas < 1){
            throw new RuntimeException();
        }
        return equipamentoRepositorio.gerarRelatorioManutencaoPreventiva(contagemMinimaFalhas);
    }
}
