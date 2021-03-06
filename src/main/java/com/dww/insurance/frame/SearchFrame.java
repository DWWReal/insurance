package com.dww.insurance.frame;

import com.dww.insurance.domain.Damage;
import com.dww.insurance.domain.DamageInfo;
import com.dww.insurance.domain.DamageReport;
import com.dww.insurance.domain.Person;
import com.dww.insurance.domain.QueryParam;
import com.dww.insurance.service.SearchRepository;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SearchFrame extends JPanel {

    private JTextField surnameTextField;
    private JTextField ownerTextField;
    private JTextField bodyTextField;
    private SearchRepository searchRepository;
    private DefaultListModel<Person> listModel = new DefaultListModel<>();
    private IApplication app;

    private JLabel driverSurname;
    private JLabel driverName;
    private JLabel driverPassId;
    private JLabel driverAddress;
    private JLabel driverPhone;
    private JLabel vehicleModel;
    private JLabel vehicleType;
    private JLabel vehicleBodyId;

    private JLabel wIcon;


    public SearchFrame(IApplication app) {
        this.app = app;
        searchRepository = new SearchRepository();
        initialize();
    }

    public void initialize() {
        removeAll();
        setLayout(null);
        JList<Person> list = new JList<>(listModel);
        list.setBounds(25, 114, 170, 402);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList source = (JList)e.getSource();
                Person selectedPerson = (Person) source.getSelectedValue();
                searchDriverInfo(selectedPerson);
            }


        });

        add(list);

        initSearchTab();

        initDriverInfoTab();

        initVehicleTab();

        JLabel lblDamage = new JLabel("Damage Info:");
        lblDamage.setBounds(260, 300, 100, 14);
        add(lblDamage);

        BufferedImage wPic = null;
        try {
            wPic = ImageIO.read(getClass().getClassLoader().getResource("AutoShema.jpg"));
//            wPic = ImageIO.read(new File());

            ImageIcon imageIcon = new ImageIcon(wPic); // load the image to a imageIcon
            Image image = imageIcon.getImage(); // transform it
            Image newimg = image.getScaledInstance(400, 250,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
            imageIcon = new ImageIcon(newimg);

            wIcon = new JLabel(imageIcon);
            wIcon.setLayout(null);
            wIcon.setBounds(260, 320, 400, 250);
            add(wIcon);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Label label1 = new Label(){
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.red);
                g.drawOval(400, 400, 100, 100);
            }
        };
        add(label1);

        populateDamageReport(null);

        JSeparator separator = new JSeparator();
        separator.setBounds(0, 75, 784, 2);
        add(separator);

        JLabel lblSearchResult = new JLabel("Search Result :");
        lblSearchResult.setBounds(25, 85, 100, 14);
        add(lblSearchResult);

//        JButton btnChange = new JButton("Change Info");
//        btnChange.setBounds(652, 493, 112, 23);
//        add(btnChange);

        JSeparator separator_2 = new JSeparator();
        separator_2.setBounds(205, 198, 579, 2);
        add(separator_2);

        JSeparator separator_3 = new JSeparator();
        separator_3.setBounds(205, 290, 579, 2);
        add(separator_3);

        setVisible(true);
    }

    private void initVehicleTab() {
        JLabel lblVehicleInfo = new JLabel("Vehicle Info:");
        lblVehicleInfo.setBounds(260, 211, 174, 14);
        add(lblVehicleInfo);

        vehicleModel = new JLabel();
        vehicleModel.setBounds(260, 236, 200, 14);
        add(vehicleModel);

        vehicleType = new JLabel();
        vehicleType.setBounds(500, 236, 130, 14);
        add(vehicleType);

        vehicleBodyId = new JLabel();
        vehicleBodyId.setBounds(260, 265, 300, 14);
        add(vehicleBodyId);
    }

    private void initDriverInfoTab() {
        JLabel driverInfoLabel = new JLabel("Driver Info:");
        driverInfoLabel.setBounds(260, 75, 145, 35);
        add(driverInfoLabel);

        driverSurname = new JLabel();
        driverSurname.setBounds(260, 115, 201, 14);
        add(driverSurname);

        driverName = new JLabel();
        driverName.setBounds(260, 143, 201, 14);
        add(driverName);

//        JLabel lblLastName = new JLabel("Patronymic ");
//        lblLastName.setBounds(504, 173, 220, 14);
//        add(lblLastName);

        driverPassId = new JLabel();
        driverPassId.setBounds(260, 173, 300, 14);
        add(driverPassId);

        driverAddress = new JLabel();
        driverAddress.setBounds(500, 143, 220, 14);
        add(driverAddress);

        driverPhone = new JLabel();
        driverPhone.setBounds(500, 115, 220, 14);
        add(driverPhone);
    }

    private void initSearchTab() {
        JLabel lblNewLabel = new JLabel("Surname");
        lblNewLabel.setBounds(25, 42, 57, 14);
        add(lblNewLabel);

        surnameTextField = new JTextField();
        surnameTextField.setBounds(81, 39, 86, 20);
        add(surnameTextField);
        surnameTextField.setColumns(10);

        JLabel lblOwnerId = new JLabel("Owner ID");
        lblOwnerId.setBounds(189, 42, 77, 14);
        add(lblOwnerId);

        ownerTextField = new JTextField();
        ownerTextField.setBounds(260, 39, 86, 20);
        add(ownerTextField);
        ownerTextField.setColumns(10);

        JLabel lblBodyId = new JLabel("Body ID");
        lblBodyId.setBounds(374, 42, 57, 14);
        add(lblBodyId);

        bodyTextField = new JTextField();
        bodyTextField.setBounds(422, 39, 86, 20);
        add(bodyTextField);
        bodyTextField.setColumns(10);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(553, 42, 89, 23);
        btnSearch.addActionListener(event -> search());
        add(btnSearch);

        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(e -> {
            SearchFrame.this.updateUI();
            app.edit();
        });
        btnAdd.setBounds(652, 42, 87, 23);
        add(btnAdd);
    }

    private void search() {
        listModel.clear();
        if (surnameTextField.getText().isEmpty() && ownerTextField.getText().isEmpty() && bodyTextField.getText().isEmpty()) {
            searchRepository
                .searchAll()
                .forEach(person -> listModel.addElement(person));
        } else {
            searchRepository
                .find(new QueryParam(surnameTextField.getText(), ownerTextField.getText(), bodyTextField.getText()))
                .forEach(person -> listModel.addElement(person));
        }
    }

    private void searchDriverInfo(Person selectedPerson) {
        DamageReport damageReport = searchRepository.searchDriverInfo(selectedPerson.getPersonId());
        populateDamageReport(damageReport);
    }

    private void populateDamageReport(DamageReport report) {
        driverName.setText("Name : " + (report == null ? "" : report.getDriverInfo().getName()));
        driverPassId.setText("Pass ID : " + (report == null ? "" : report.getDriverInfo().getPassId()));
        driverSurname.setText("Last Name : " + (report == null ? "" : report.getDriverInfo().getLastName()));
        driverAddress.setText("Address : " + (report == null ? "" : report.getDriverInfo().getAddress()));
        driverPhone.setText("Phone : " + (report == null ? "" : report.getDriverInfo().getPhone()));

        vehicleModel.setText("Model : " + (report == null ? "" : report.getVehicleInfo().getModel()));
        vehicleType.setText("Type : " + (report == null ? "" : report.getVehicleInfo().getType()));
        vehicleBodyId.setText("Body ID : " + (report == null ? "" : report.getVehicleInfo().getBodyId()));
        damageZone(report == null ? null : report.getDamageInfo());
    }

    private void damageZone(DamageInfo damageInfo) {
        if (damageInfo != null) wIcon.removeAll();
        wIcon.add(zoneLabel(348, 115, damageInfo != null && damageInfo.getDamage().getDamageZone()[4])); // zone 5
        wIcon.add(zoneLabel(285, 115, damageInfo != null && damageInfo.getDamage().getDamageZone()[5])); // zone 6
        wIcon.add(zoneLabel(211, 115, damageInfo != null && damageInfo.getDamage().getDamageZone()[10])); // zone 11
        wIcon.add(zoneLabel(102, 115, damageInfo != null && damageInfo.getDamage().getDamageZone()[11])); // zone 12
        wIcon.add(zoneLabel(12, 115, damageInfo != null && damageInfo.getDamage().getDamageZone()[12])); // zone 13

        wIcon.add(zoneLabel(119, 203, damageInfo != null && damageInfo.getDamage().getDamageZone()[0])); // zone 1
        wIcon.add(zoneLabel(171, 203, damageInfo != null && damageInfo.getDamage().getDamageZone()[1])); // zone 2
        wIcon.add(zoneLabel(221, 203, damageInfo != null && damageInfo.getDamage().getDamageZone()[2])); // zone 3
        wIcon.add(zoneLabel(273, 203, damageInfo != null && damageInfo.getDamage().getDamageZone()[3])); // zone 4

        wIcon.add(zoneLabel(124, 28, damageInfo != null && damageInfo.getDamage().getDamageZone()[9])); // zone 10
        wIcon.add(zoneLabel(171, 28, damageInfo != null && damageInfo.getDamage().getDamageZone()[8])); // zone 9
        wIcon.add(zoneLabel(221, 28, damageInfo != null && damageInfo.getDamage().getDamageZone()[7])); // zone 8
        wIcon.add(zoneLabel(273, 28, damageInfo != null && damageInfo.getDamage().getDamageZone()[6])); // zone 7
        wIcon.repaint();
    }

    private JLabel zoneLabel(int x, int y, boolean enabled) {
        SelectionPoint selectionPoint13 = new SelectionPoint(enabled);
        JLabel zoneLabel = new JLabel(selectionPoint13);

            zoneLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectionPoint13.switchColor();
                    zoneLabel.repaint();
                }
            });
        zoneLabel.setBounds(x,y,25,25);
        return zoneLabel;
    }

}
