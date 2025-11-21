package org.example.repositorio;

import org.example.database.Conexao;
import org.example.dto.RelatorioParadaDTO;
import org.example.model.Falha;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FalhaRepositorio {
    public Falha criarFalha(Falha falha) throws SQLException {
        String query = """
                INSERT INTO Falha
                (equipamentoId
                ,dataHoraOcorrencia
                ,descricao
                ,criticidade
                ,status
                ,tempoParadaHoras)
                VALUES
                (?,?,?,?,?,?)
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            stmt.setLong(1, falha.getEquipamentoId());
            stmt.setTimestamp(2, Timestamp.valueOf(falha.getDataHoraOcorrencia()));
            stmt.setString(3, falha.getDescricao());
            stmt.setString(4, falha.getCriticidade());
            stmt.setString(5, falha.getStatus());
            stmt.setBigDecimal(6,falha.getTempoParadaHoras());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if(rs.next()){
                falha.setId(rs.getLong(1));
                return falha;
            }

        }
        return falha;
    }

    public List<Falha> buscarFalhasCriticasAbertas() throws SQLException{
        List<Falha> falhas = new ArrayList<>();
        String query = """
                SELECT  id
                        ,equipamentoId
                        ,dataHoraOcorrencia
                        ,descricao
                        ,criticidade
                        ,status
                        ,tempoParadaHoras
                FROM Falha
                WHERE criticidade = 'CRITICA'
                AND status  = 'ABERTA'
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                Long id = rs.getLong("id");
                Long equipamentoId = rs.getLong("equipamentoId");
                LocalDateTime dataHoraOcorrencia = rs.getTimestamp("dataHoraOcorrencia").toLocalDateTime();
                String descricao = rs.getString("descricao");
                String criticidade = rs.getString("criticidade");
                String status = rs.getString("status");
                BigDecimal tempoParadaHoras = rs.getBigDecimal("tempoParadaHoras");

                falhas.add(new Falha(
                        id,
                        equipamentoId,
                        dataHoraOcorrencia,
                        descricao,
                        criticidade,
                        status,
                        tempoParadaHoras
                ));
            }
        }
        return falhas;
    }

    public List<Falha> buscarTodosEquipamentos() throws SQLException{
        List<Falha> falhas = new ArrayList<>();
        String query = """
                SELECT  id
                        ,equipamentoId
                        ,dataHoraOcorrencia
                        ,descricao
                        ,criticidade
                        ,status
                        ,tempoParadaHoras
                FROM Falha
                WHERE criticidade = 'CRITICA'
                AND status  = 'ABERTA'
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                Long id = rs.getLong("id");
                Long equipamentoId = rs.getLong("equipamentoId");
                LocalDateTime dataHoraOcorrencia = rs.getTimestamp("dataHoraOcorrencia").toLocalDateTime();
                String descricao = rs.getString("descricao");
                String criticidade = rs.getString("criticidade");
                String status = rs.getString("status");
                BigDecimal tempoParadaHoras = rs.getBigDecimal("tempoParadaHoras");

                falhas.add(new Falha(
                        id,
                        equipamentoId,
                        dataHoraOcorrencia,
                        descricao,
                        criticidade,
                        status,
                        tempoParadaHoras
                ));
            }
        }
        return falhas;
    }

    public List<RelatorioParadaDTO> buscarTodosRelatoriosParada() throws SQLException{
        List<RelatorioParadaDTO> falhas = new ArrayList<>();
        String query = """
                SELECT   f.equipamentoId
                        , e.nome
                        , f.tempoParadaHoras
                FROM Falha f
                JOIN Equipamento e
                ON f.equipamentoID = e.id
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                Long equipamentoId = rs.getLong("equipamentoId");
                String nome = rs.getString("nome");
                double tempoParadaHoras = rs.getDouble("tempoParadaHoras");

                falhas.add(new RelatorioParadaDTO(
                                equipamentoId,
                                nome,
                                tempoParadaHoras
                        )
                );
            }
        }
        return falhas;
    }

    public boolean falhaExiste(Long id) throws SQLException{
        String query = """
                SELECT COUNT(0)
                FROM Falha
                WHERE id = ?
                """;
        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return rs.getLong(1) > 0;
            }
        }
        return false;
    }

    public void atualizarStatus(Long id, String status) throws SQLException{
        String query = """
                UPDATE Falha
                set status = ?
                WHERE id  = ?
                """;
        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, status);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public String buscarCriticidadeFalhaPorId(Long id) throws SQLException{
        String query = """
                SELECT criticidade
                FROM Falha
                WHERE id = ?
                """;
        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1,id);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getString("criticidade");
            }
        }
        return "";
    }

    public Falha buscarFalhaPorId(Long id) throws SQLException{
        String query = """
                SELECT  id
                        ,equipamentoId
                        ,dataHoraOcorrencia
                        ,descricao
                        ,criticidade
                        ,status
                        ,tempoParadaHoras
                FROM Falha
                WHERE id = ?
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setLong(1,id);
            ResultSet rs = stmt.executeQuery();


            while(rs.next()){
                Long equipamentoId = rs.getLong("equipamentoId");
                LocalDateTime dataHoraOcorrencia = rs.getTimestamp("dataHoraOcorrencia").toLocalDateTime();
                String descricao = rs.getString("descricao");
                String criticidade = rs.getString("criticidade");
                String status = rs.getString("status");
                BigDecimal tempoParadaHoras = rs.getBigDecimal("tempoParadaHoras");

                return new Falha(
                        id,
                        equipamentoId,
                        dataHoraOcorrencia,
                        descricao,
                        criticidade,
                        status,
                        tempoParadaHoras
                );
            }
        }
        return null;
    }

}
