import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataStreamsGUI extends JFrame {

    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton, searchButton, quitButton;
    private Path loadedFilePath;
    private List<String> originalLines; // Keep a copy of original file lines

    public DataStreamsGUI() {
        setTitle("Data Streams File Searcher");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window

        initUI();
    }

    private void initUI() {
        originalTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        JScrollPane originalScroll = new JScrollPane(originalTextArea);

        filteredTextArea = new JTextArea();
        filteredTextArea.setEditable(false);
        JScrollPane filteredScroll = new JScrollPane(filteredTextArea);

        searchField = new JTextField(20);

        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Search String:"));
        topPanel.add(searchField);
        topPanel.add(loadButton);
        topPanel.add(searchButton);
        topPanel.add(quitButton);

        JPanel textPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        textPanel.add(originalScroll);
        textPanel.add(filteredScroll);

        add(topPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);

        // Button Actions
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFile();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchInFile();
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser();
        int option = chooser.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            loadedFilePath = chooser.getSelectedFile().toPath();
            try (Stream<String> lines = Files.lines(loadedFilePath)) {
                originalLines = lines.collect(Collectors.toList());
                displayOriginalText();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
            }
        }
    }

    private void displayOriginalText() {
        StringBuilder sb = new StringBuilder();
        for (String line : originalLines) {
            sb.append(line).append("\n");
        }
        originalTextArea.setText(sb.toString());
        filteredTextArea.setText(""); // Clear filtered area
    }

    private void searchInFile() {
        if (loadedFilePath == null) {
            JOptionPane.showMessageDialog(this, "Please load a file first!");
            return;
        }

        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search string!");
            return;
        }

        try (Stream<String> lines = Files.lines(loadedFilePath)) {
            List<String> filtered = lines
                    .filter(line -> line.contains(searchTerm))
                    .collect(Collectors.toList());

            StringBuilder sb = new StringBuilder();
            for (String line : filtered) {
                sb.append(line).append("\n");
            }
            filteredTextArea.setText(sb.toString());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataStreamsGUI().setVisible(true);
        });
    }
}
