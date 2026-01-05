package id.ac.campus.antiexam.data;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RuangUjianData {

    public void createRoom(String name) throws Exception {
        String sql = "INSERT INTO ruangan (name) VALUES (?)";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        }
    }

    public List<String> listRooms() throws Exception {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM ruangan ORDER BY name ASC";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        }
        return list;
    }
}

