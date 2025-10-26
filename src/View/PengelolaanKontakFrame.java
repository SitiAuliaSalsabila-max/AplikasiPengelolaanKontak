/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import Model.Kontak;
import controller.KontakController;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;

/**
 * Frame utama untuk manajemen kontak.
 * Menyediakan fitur tambah, edit, hapus, ekspor, dan impor CSV.
 */
public class PengelolaanKontakFrame extends javax.swing.JFrame {

    private DefaultTableModel model;
    private KontakController controller;

    /**
     * Creates new form PengelolaanKontakFrame
     */
    public PengelolaanKontakFrame() {
        initComponents();
        setTitle("Aplikasi Manajemen Kontak");
        setLocationRelativeTo(null);

        controller = new KontakController();
        model = new DefaultTableModel(new String[]{"ID", "Nama", "Nomor Telepon", "Kategori"}, 0);
        jTable1.setModel(model);

        loadContacts();
    }

    // ======================== LOGIKA UTAMA ============================= //

    private void loadContacts() {
        try {
            model.setRowCount(0);
            List<Kontak> contacts = controller.getAllContacts();

            int rowNumber = 1;
            for (Kontak contact : contacts) {
                model.addRow(new Object[]{
                        contact.getId(),
                        contact.getNama(),
                        contact.getNomorTelepon(),
                        contact.getKategori()
                });
                rowNumber++;
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void addContact() {
        String nama = txtNama.getText().trim();
        String nomorTelepon = txtNomorTelepon.getText().trim();
        String kategori = (String) cmbKatagori.getSelectedItem();

        if (!validatePhoneNumber(nomorTelepon)) {
            return;
        }

        try {
            if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
                JOptionPane.showMessageDialog(this,
                        "Kontak dengan nomor telepon ini sudah ada.",
                        "Kesalahan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.addContact(nama, nomorTelepon, kategori);
            loadContacts();
            JOptionPane.showMessageDialog(this, "Kontak berhasil ditambahkan!");
            clearInputFields();
        } catch (SQLException ex) {
            showError("Gagal menambahkan kontak: " + ex.getMessage());
        }
    }

    private void editContact() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diperbarui.");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);
        String nama = txtNama.getText().trim();
        String nomorTelepon = txtNomorTelepon.getText().trim();
        String kategori = (String) cmbKatagori.getSelectedItem();

        if (!validatePhoneNumber(nomorTelepon)) return;

        try {
            if (controller.isDuplicatePhoneNumber(nomorTelepon, id)) {
                JOptionPane.showMessageDialog(this,
                        "Nomor telepon ini sudah terdaftar.",
                        "Kesalahan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            controller.updateContact(id, nama, nomorTelepon, kategori);
            loadContacts();
            JOptionPane.showMessageDialog(this, "Kontak berhasil diperbarui!");
            clearInputFields();
        } catch (SQLException ex) {
            showError("Gagal memperbarui kontak: " + ex.getMessage());
        }
    }

    private void deleteContact() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin dihapus.");
            return;
        }

        int id = (int) model.getValueAt(selectedRow, 0);

        try {
            controller.deleteContact(id);
            loadContacts();
            JOptionPane.showMessageDialog(this, "Kontak berhasil dihapus!");
            clearInputFields();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan sebagai CSV");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (FileWriter writer = new FileWriter(fileToSave + ".csv")) {
                writer.append("ID,Nama,Nomor Telepon,Kategori\n");

                for (int i = 0; i < model.getRowCount(); i++) {
                    writer.append(model.getValueAt(i, 0).toString()).append(",");
                    writer.append(model.getValueAt(i, 1).toString()).append(",");
                    writer.append(model.getValueAt(i, 2).toString()).append(",");
                    writer.append(model.getValueAt(i, 3).toString()).append("\n");
                }

                JOptionPane.showMessageDialog(this, "Data berhasil disimpan ke CSV!");
            } catch (IOException e) {
                showError("Gagal menyimpan CSV: " + e.getMessage());
            }
        }
    }

    private void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih file CSV untuk diimpor");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();

            try (BufferedReader reader = new BufferedReader(new FileReader(fileToOpen))) {
                String header = reader.readLine();
                if (!validateCSVHeader(header)) {
                    showError("Header CSV tidak valid. Pastikan urutannya: ID,Nama,Nomor Telepon,Kategori");
                    return;
                }

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 4) {
                        String nama = data[1];
                        String nomorTelepon = data[2];
                        String kategori = data[3];
                        controller.addContact(nama, nomorTelepon, kategori);
                    }
                }

                loadContacts();
                JOptionPane.showMessageDialog(this, "Data berhasil diimpor dari CSV!");
            } catch (IOException | SQLException e) {
                showError("Gagal mengimpor CSV: " + e.getMessage());
            }
        }
    }

    private boolean validateCSVHeader(String header) {
        return header.trim().equalsIgnoreCase("ID,Nama,Nomor Telepon,Kategori");
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            showError("Nomor telepon tidak boleh kosong.");
            return false;
        }
        if (!phoneNumber.matches("\\d+")) {
            showError("Nomor telepon hanya boleh berisi angka.");
            return false;
        }
        if (phoneNumber.length() < 8 || phoneNumber.length() > 15) {
            showError("Nomor telepon harus 8–15 digit.");
            return false;
        }
        return true;
    }

    private void clearInputFields() {
        txtNama.setText("");
        txtNomorTelepon.setText("");
        cmbKatagori.setSelectedIndex(0);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    // ======================== EVENT HANDLER ============================= //

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {
        addContact();
    }

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {
        editContact();
    }

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        deleteContact();
    }

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {
        exportToCSV();
    }

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {
        importFromCSV();
    }

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            txtNama.setText(model.getValueAt(selectedRow, 1).toString());
            txtNomorTelepon.setText(model.getValueAt(selectedRow, 2).toString());
            cmbKatagori.setSelectedItem(model.getValueAt(selectedRow, 3).toString());
        }
    }

    // ======================== AUTO-GENERATED GUI ============================= //

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        lblNamaKontak = new javax.swing.JLabel();
        lblNomorTelepon = new javax.swing.JLabel();
        lblKatagori = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();
        txtNomorTelepon = new javax.swing.JTextField();
        cmbKatagori = new javax.swing.JComboBox<>();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        btnExport = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblJudul.setText("Aplikasi Manajemen Kontak");
        lblNamaKontak.setText("Nama:");
        lblNomorTelepon.setText("Nomor Telepon:");
        lblKatagori.setText("Kategori:");

        cmbKatagori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Teman", "Keluarga", "Kerja"}));

        btnTambah.setText("Tambah");
        btnTambah.addActionListener(this::btnTambahActionPerformed);

        btnEdit.setText("Edit");
        btnEdit.addActionListener(this::btnEditActionPerformed);

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(this::btnHapusActionPerformed);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Nama", "Nomor Telepon", "Kategori"}
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        btnExport.setText("Export CSV");
        btnExport.addActionListener(this::btnExportActionPerformed);

        btnImport.setText("Import CSV");
        btnImport.addActionListener(this::btnImportActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblJudul)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(lblNamaKontak)
                                                                        .addComponent(lblNomorTelepon)
                                                                        .addComponent(lblKatagori))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(txtNama)
                                                                        .addComponent(txtNomorTelepon)
                                                                        .addComponent(cmbKatagori, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(btnTambah)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnEdit)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnHapus)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                                                                .addComponent(btnExport)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnImport)))
                                                .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblJudul)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblNamaKontak)
                                        .addComponent(txtNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblNomorTelepon)
                                        .addComponent(txtNomorTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblKatagori)
                                        .addComponent(cmbKatagori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnTambah)
                                        .addComponent(btnEdit)
                                        .addComponent(btnHapus)
                                        .addComponent(btnExport)
                                        .addComponent(btnImport))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new PengelolaanKontakFrame().setVisible(true));
    }

    // Variables declaration
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbKatagori;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblKatagori;
    private javax.swing.JLabel lblNamaKontak;
    private javax.swing.JLabel lblNomorTelepon;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtNomorTelepon;
    // End of variables declaration
}
