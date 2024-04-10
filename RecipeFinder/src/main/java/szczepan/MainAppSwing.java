package szczepan;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;





public class MainAppSwing extends JFrame {

    private final JPanel centerPanel; // Referencja do centralnego panelu
    private final JPanel rightPanel; // Referencja do prawego panelu z lodówką
    private static final Logger logger = LogManager.getLogger(MainAppSwing.class);

    public MainAppSwing() {

        setTitle("Aplikacja Zarządzanie Przepisami");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 600);
        zarzadzanieMySql zarzadzanie = new zarzadzanieMySql();
        zarzadzanie.wyczyscLodowkePrzyUruchomieniu();


        Color Scarlet = new Color(163, 23, 0);
        Color Orange = new Color(251, 170, 96);
        Color Coral = new Color(246, 123, 80);
        Color Peach = new Color(251, 196, 144);

        //Ustawienie rozmiaru dla przycisków
        Dimension buttonDimension = new Dimension(140, 30);
        // Lewa sekcja z przyciskami
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(160, 600));
        leftPanel.setBackground(Orange);
        leftPanel.setBorder(BorderFactory.createLineBorder(Scarlet, 2));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JButton przyciskPokazSkladniki = new JButton("Pokaż Składniki");
        przyciskPokazSkladniki.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<List<String>, Void>() {
                    @Override
                    protected List<String> doInBackground() throws Exception {
                        zarzadzanieMySql zarzadzanie = new zarzadzanieMySql();
                        List<String> skladniki = zarzadzanie.pobierzUnikatoweNazwySkladnikow();
                        Collections.sort(skladniki);
                        return skladniki;
                    }

                    @Override
                    protected void done() {
                        try {
                            List<String> skladniki = get();
                            pokazUnikatoweNazwySkladnikow(skladniki);
                        } catch (Exception e) {
                            logger.error("Błąd podczas przetwarzania listy składników", e);
                        }
                    }
                }.execute();
            }
        });


// Przycisk Wyczyść Lodówkę
        JButton przyciskwyczyscLodowke = new JButton("Wyczyść lodówkę");
        przyciskwyczyscLodowke.addActionListener(e -> {

            zarzadzanie.wyczyscLodowke();
            odswiezLodowke();
        });


        JButton przyciskZnajdzPrzepis = new JButton("Znajdź Przepis");
        przyciskZnajdzPrzepis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wyswietlPrzepisyWedlugKategorii(centerPanel);
            }
        });

        JButton przyciskZainspirujSie = new JButton("Zainispiruj się");
        przyciskZainspirujSie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] losowyPrzepis = zarzadzanie.losujPrzepis();
                if (losowyPrzepis != null) {
                    pokazSzczegolyPrzepisu(losowyPrzepis[0]);
                } else {
                    // Obsługa sytuacji, gdy nie udało się pobrać przepisu
                }
            }
        });
        leftPanel.add(przyciskZainspirujSie);

        JButton[] przyciski = {przyciskPokazSkladniki, przyciskwyczyscLodowke, przyciskZnajdzPrzepis, przyciskZainspirujSie};

        for (JButton button : przyciski) {
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setBackground(Scarlet);
            button.setForeground(Peach);
            button.setContentAreaFilled(true);
            button.setFocusPainted(false);
            button.setPreferredSize(buttonDimension);
            button.setMaximumSize(buttonDimension);
            button.setMinimumSize(buttonDimension);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    JButton sourceButton = (JButton) e.getSource();
                    sourceButton.setBackground(Coral);
                    sourceButton.setForeground(Scarlet);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    JButton sourceButton = (JButton) e.getSource();
                    sourceButton.setBackground(Scarlet);
                    sourceButton.setForeground(Peach);
                }
            });


            // Dodanie przycisku do panelu
            leftPanel.add(Box.createVerticalStrut(5));
            leftPanel.add(button);
        }


// Dodanie odstępu na końcu panelu, jeśli potrzebne
        leftPanel.add(Box.createVerticalGlue());

        // Prawa sekcja z lodówką
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS)); // Ustawienie pionowego układu
        odswiezLodowke();
        rightPanel.setPreferredSize(new Dimension(160, 600));
        rightPanel.setBorder(BorderFactory.createLineBorder(Scarlet, 2));
        rightPanel.setBackground(Orange);

        centerPanel = new JPanel(new BorderLayout());
        JLabel witaj = new JLabel("Witaj! Dodaj składniki do lodówki, a następnie znajdź przepis.");
        witaj.setHorizontalAlignment(JLabel.CENTER);
        witaj.setForeground(new Color(163, 23, 0));
        witaj.setFont(new Font("SansSerif", Font.BOLD, 16));
        centerPanel.add(witaj);
        centerPanel.setBackground(new Color(251, 196, 144));

        // Dodanie paneli do głównego okna
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);

        // Widoczność i układ
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void pokazUnikatoweNazwySkladnikow(List<String> skladniki) {
        centerPanel.removeAll();
        centerPanel.setLayout(new BorderLayout());

        JPanel skladnikiPanel = new JPanel();
        skladnikiPanel.setLayout(new BoxLayout(skladnikiPanel, BoxLayout.Y_AXIS));
        skladnikiPanel.setBackground(new Color (251,196,144));

        List<JCheckBox> checkboxes = new ArrayList<>();
        for (String skladnik : skladniki) {
            JCheckBox checkBox = new JCheckBox(skladnik);
            checkBox.setOpaque(false);
            checkBox.setForeground(new Color(163, 23, 0));
            checkBox.setFont(new Font("Dialog", Font.BOLD, 14));
            checkboxes.add(checkBox);
            skladnikiPanel.add(checkBox);
        }

        JScrollPane scrollPane = new JScrollPane(skladnikiPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(15, Integer.MAX_VALUE));

        int unitIncrement = 16; // większa wartość = szybsze przewijanie jednostkowe
        verticalScrollBar.setUnitIncrement(unitIncrement);

        // Zmiana kolorów paska przewijania
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(246, 123, 80); // kolor "kciuka"
                this.trackColor = new Color(251, 170, 96); // kolor tła
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JButton dodajPrzycisk = new JButton("Dodaj do lodówki");
        dodajPrzycisk.setOpaque(true);
        dodajPrzycisk.setBorderPainted(false);
        dodajPrzycisk.setBackground(new Color(163, 23, 0));
        dodajPrzycisk.setForeground(new Color (251,196,144));
        dodajPrzycisk.setContentAreaFilled(true);
        dodajPrzycisk.setFocusPainted(false);

        dodajPrzycisk.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JButton sourceButton = (JButton) e.getSource();
                sourceButton.setBackground(new Color(246, 123, 80));
                sourceButton.setForeground(new Color(163, 23, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                JButton sourceButton = (JButton) e.getSource();
                sourceButton.setBackground(new Color(163, 23, 0));
                sourceButton.setForeground(new Color (251,196,144));
            }
        });

        dodajPrzycisk.addActionListener(e -> {
            List<String> wybraneSkladniki = new ArrayList<>();
            // Dodawanie "soli" i "pieprzu" do tabeli "Lodowka"
            wybraneSkladniki.add("sól");
            wybraneSkladniki.add("pieprz");
            for (JCheckBox checkBox : checkboxes) {
                if (checkBox.isSelected()) {
                    wybraneSkladniki.add(checkBox.getText());
                    checkBox.setSelected(false); // Odznacz checkbox po dodaniu składnika
                }
            }
            zarzadzanieMySql zarzadzanie = new zarzadzanieMySql();
            zarzadzanie.dodajDoLodowki(wybraneSkladniki);
            odswiezLodowke();
        });
        centerPanel.add(dodajPrzycisk, BorderLayout.SOUTH);

        centerPanel.revalidate();
        centerPanel.repaint();

    }


    private void odswiezLodowke() {
        rightPanel.removeAll();
        rightPanel.add(Box.createVerticalStrut(5));
        JLabel lodowka = new JLabel("Lodówka:");
        lodowka.setAlignmentX(Component.CENTER_ALIGNMENT);
        lodowka.setForeground(new Color(163, 23, 0));
        lodowka.setFont(new Font("SansSerif", Font.BOLD, 14));
        rightPanel.add(lodowka);
        zarzadzanieMySql zarzadzanie = new zarzadzanieMySql();
        List<String> skladniki = zarzadzanie.pobierzSkladnikiZLodowki();
        for (String skladnik : skladniki) {
            JLabel label = new JLabel("• " + skladnik);
            label.setForeground(new Color(163, 23, 0));
            label.setFont(new Font("SansSerif", Font.BOLD, 12));
            lodowka.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            rightPanel.add(label);

        }

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void pokazSzczegolyPrzepisu(String nazwaPrzepisu) {
        zarzadzanieMySql zarzadzanie = new zarzadzanieMySql();
        List<String> skladniki = zarzadzanie.pobierzSkladnikiDlaPrzepisu(nazwaPrzepisu);

        JDialog dialog = new JDialog(this, "Szczegóły Przepisu", true);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(163, 23, 0)); // Ustaw kolor tła
        JLabel name = new JLabel("Przepis: " + nazwaPrzepisu);
        name.setForeground(new Color(251, 196, 144));
        dialog.add(name, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(new Color(251, 196, 144));
        textArea.setForeground(new Color(163, 23, 0));
        for (String skladnik : skladniki) {
            textArea.append("• " + skladnik + "\n");
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(15, Integer.MAX_VALUE));

        int unitIncrement = 16; // większa wartość = szybsze przewijanie jednostkowe
        verticalScrollBar.setUnitIncrement(unitIncrement);

        // Zmiana kolorów paska przewijania
        verticalScrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(246, 123, 80); // kolor "kciuka"
                this.trackColor = new Color(251, 170, 96); // kolor tła
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        dialog.add(scrollPane, BorderLayout.CENTER);

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return webScraper.pobierzLinkDoPrzepisu(nazwaPrzepisu);
            }

            @Override
            protected void done() {
                try {
                    String linkDoPrzepisu = get();
                    if (linkDoPrzepisu != null) {
                        JLabel linkLabel = new JLabel("<html>Link do przepisu: <a href=\"" + linkDoPrzepisu + "\">" + linkDoPrzepisu + "</a></html>");
                        linkLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        linkLabel.setForeground(new Color(163, 23, 0));
                        linkLabel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                try {
                                    Desktop.getDesktop().browse(new URI(linkDoPrzepisu));
                                } catch (Exception ex) {
                                    logger.error("Błąd podczas otwierania linku", ex);
                                }
                            }
                        });
                        dialog.add(linkLabel, BorderLayout.SOUTH);
                        dialog.revalidate();
                        dialog.repaint();
                    }
                } catch (Exception e) {
                    logger.error("Błąd podczas pobierania linku do przepisu", e);
                }
            }
        }.execute();

        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    public class CenterTableCellRenderer extends DefaultTableCellRenderer {
        public CenterTableCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER); // Wyśrodkowanie tekstu
            setForeground(new Color(163, 23, 0)); // Zmiana koloru tekstu
            setFont(new Font("SansSerif", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return this;
        }
    }

    private void wyswietlPrzepisyWedlugKategorii(JPanel centerPanel) {
        zarzadzanieMySql zarzadzanie = new zarzadzanieMySql();
        List<String> skladniki = zarzadzanie.pobierzSkladnikiZLodowki();

        if (skladniki.isEmpty()) {
            // Lodówka jest pusta
            centerPanel.removeAll();
            centerPanel.setLayout(new BorderLayout());
            JLabel pustalodowka = new JLabel("Lodówka jest pusta. Dodaj składniki do lodówki!");
            pustalodowka.setHorizontalAlignment(JLabel.CENTER);
            pustalodowka.setForeground(new Color(163, 23, 0));
            pustalodowka.setFont(new Font("SansSerif", Font.BOLD, 16));
            centerPanel.add(pustalodowka);
        } else {
            // Lodówka zawiera składniki, więc wyświetlamy przepisy
            List<String[]> przepisy = zarzadzanie.znajdzPrzepisyWedlugKategorii();

            RecipeTableModel model = new RecipeTableModel();
            model.setData(przepisy);

            JTable table = new JTable(model);
            CenterTableCellRenderer centerRenderer = new CenterTableCellRenderer();
            for (int i = 0; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

            Color tableBackground = new Color (251,196,144);
            table.setBackground(tableBackground);
            table.setFillsViewportHeight(true);
            table.setGridColor(new Color(251, 170, 96));

            JTableHeader header = table.getTableHeader();
            header.setBackground(new Color(163, 23, 0)); // Ustawienie koloru tła dla nagłówka
            header.setForeground(new Color (251,196,144)); // Ustawienie koloru tekstu dla nagłówka
            Font headerFont = new Font("SansSerif", Font.BOLD, 12); // Ustaw odpowiednią nazwę czcionki i rozmiar
            header.setFont(headerFont);

            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        JTable target = (JTable) e.getSource();
                        int row = target.getSelectedRow();
                        String nazwaPrzepisu = (String) target.getModel().getValueAt(row, 0);
                        pokazSzczegolyPrzepisu(nazwaPrzepisu);
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            Component corner = new JPanel();
            corner.setBackground(new Color(251, 170, 96));
            scrollPane.setCorner(JScrollPane.UPPER_TRAILING_CORNER, corner);
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setPreferredSize(new Dimension(15, Integer.MAX_VALUE));

            int unitIncrement = 16; // większa wartość = szybsze przewijanie jednostkowe
            verticalScrollBar.setUnitIncrement(unitIncrement);

            // Zmiana kolorów paska przewijania
            verticalScrollBar.setUI(new BasicScrollBarUI() {
                @Override
                protected void configureScrollBarColors() {
                    this.thumbColor = new Color(246, 123, 80); // kolor "kciuka"
                    this.trackColor = new Color(251, 170, 96); // kolor tła
                }

                @Override
                protected JButton createDecreaseButton(int orientation) {
                    return createZeroButton();
                }
                @Override
                protected JButton createIncreaseButton(int orientation) {
                    return createZeroButton();
                }
                private JButton createZeroButton() {
                    JButton button = new JButton();
                    button.setPreferredSize(new Dimension(0, 0));
                    button.setMinimumSize(new Dimension(0, 0));
                    button.setMaximumSize(new Dimension(0, 0));
                    return button;
                }
            });

            centerPanel.removeAll();
            centerPanel.setLayout(new BorderLayout());
            centerPanel.add(scrollPane, BorderLayout.CENTER);
        }
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Connection conn = DatabaseConnector.getConnection();
            if (conn != null) {
                logger.info("Uruchamianie aplikacji");
                new MainAppSwing();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Nie można nawiązać połączenia z bazą danych.",
                        "Błąd połączenia",
                        JOptionPane.ERROR_MESSAGE);
                logger.fatal("Nie można nawiązać połączenia z bazą danych! - ZAMYKAM");
            }
        });
    }

}

