package org.example.repositorio;

import org.example.database.Conexao;
import org.example.dto.EquipamentoContagemFalhasDTO;
import org.example.model.Equipamento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EquipamentoRepositorio {
    public Equipamento salvarEquipamento(Equipamento equipamento) throws SQLException {
        String query = """
                INSERT INTO Equipamento
                (nome,numeroDeSerie,areaSetor,statusOperacional)
                VALUES
                (?,?,?,?)
                """;
        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

            stmt.setString(1,equipamento.getNome());
            stmt.setString(2,equipamento.getNumeroDeSerie());
            stmt.setString(3,equipamento.getAreaSetor());
            stmt.setString(4,equipamento.getStatusOperacional());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if(rs.next()){
                equipamento.setId(rs.getLong(1));
            }
        }
        return equipamento;
    }

    public Equipamento buscarEquipamentoPorId(Long id) throws SQLException{
        String query = """
                SELECT id
                        , nome
                        , numeroDeSerie
                        , areaSetor
                        , statusOperacional
                FROM Equipamento
                WHERE id = ?
                """;
        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setLong(1,id);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return new Equipamento(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("numeroDeSerie"),
                        rs.getString("areaSetor"),
                        rs.getString("statusOperacional")
                );
            }
        }
        return null;
    }

    public boolean equipamentoExiste(Long id) throws SQLException{
        String query = """
                SELECT COUNT(0)
                FROM Equipamento
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
                UPDATE Equipamento
                set statusOperacional = ?
                WHERE id  = ?
                """;
        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, status);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public List<Equipamento> buscarEquipamentosSemFalha(LocalDate dataDe, LocalDate dataAte) throws SQLException{
        List<Equipamento> equipamentos = new ArrayList<>();
        String query = """
                SELECT   id
                        ,nome
                        ,numeroDeSerie
                        ,areaSetor
                        ,statusOperacional
                FROM Equipamento
                WHERE id NOT IN (SELECT equipamentoId
                                     FROM Falha
                                     WHERE dataHoraOcorrencia >= ?
                                     AND dataHoraOcorrencia <= ?)
                """;

        try(Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setDate(1, java.sql.Date.valueOf(dataDe));
            stmt.setDate(2, Date.valueOf(dataAte));

            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                equipamentos.add(
                        new Equipamento(
                                rs.getLong("id"),
                                rs.getString("nome"),
                                rs.getString("numeroDeSerie"),
                                rs.getString("areaSetor"),
                                rs.getString("statusOperacional")
                        )
                );
            }
        }
        return  equipamentos;
    }

    public List<EquipamentoContagemFalhasDTO> gerarRelatorioManutencaoPreventiva(int contagemMinimaFalhas) throws SQLException{
        List<EquipamentoContagemFalhasDTO> resultado = new ArrayList<>();
        String sql = """
                    SELECT
                        e.id,
                        e.nome,
                        COUNT(f.id) AS totalFalhas
                    FROM Equipamento e
                    LEFT JOIN Falha f ON f.equipamentoId = e.id
                    GROUP BY e.id, e.nome
                    HAVING COUNT(f.id) >= ?
        """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contagemMinimaFalhas);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                EquipamentoContagemFalhasDTO dto =
                        new EquipamentoContagemFalhasDTO(
                                rs.getLong("id"),
                                rs.getString("nome"),
                                rs.getInt("totalFalhas")
                        );
                resultado.add(dto);
            }
        }
        return resultado;
    }
}
