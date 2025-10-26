package controller;

import Model.Kontak;
import java.sql.SQLException;
import java.util.List;

public class KontakController {
    private KontakDAO kontakDAO;

    public KontakController() {
        kontakDAO = new KontakDAO();
    }

    public List<Kontak> getAllContacts() throws SQLException {
        return kontakDAO.getAllContacts();
    }

    public void addContact(String nama, String nomorTelepon, String kategori) throws SQLException {
        Kontak kontak = new Kontak(0, nama, nomorTelepon, kategori);
        kontakDAO.addContact(kontak);
    }

    public void updateContact(int id, String nama, String nomorTelepon, String kategori) throws SQLException {
        Kontak kontak = new Kontak(id, nama, nomorTelepon, kategori);
        kontakDAO.updateContact(kontak);
    }

    public void deleteContact(int id) throws SQLException {
        kontakDAO.deleteContact(id);
    }

    public List<Kontak> searchContacts(String keyword) throws SQLException {
        return kontakDAO.searchContacts(keyword);
    }

    public boolean isDuplicatePhoneNumber(String nomorTelepon, Integer excludeId) throws SQLException {
        return kontakDAO.isDuplicatePhoneNumber(nomorTelepon, excludeId);
    }

    private static class KontakDAO {

        public KontakDAO() {
        }

        private List<Kontak> getAllContacts() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void addContact(Kontak kontak) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void updateContact(Kontak kontak) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private void deleteContact(int id) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private List<Kontak> searchContacts(String keyword) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        private boolean isDuplicatePhoneNumber(String nomorTelepon, Integer excludeId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}
