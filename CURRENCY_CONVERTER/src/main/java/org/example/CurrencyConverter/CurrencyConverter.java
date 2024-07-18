package org.example.CurrencyConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.json.JSONObject;

// Factory Pattern: Factory class for UI components
class ComponentFactory {
    public static JComboBox<String> createCurrencyComboBox(String[] currencies) {
        return new JComboBox<>(currencies);
    }

    public static JTextField createTextField(int columns) {
        return new JTextField(columns);
    }

    public static JButton createButton(String text) {
        return new JButton(text);
    }

    public static JLabel createLabel(String text) {
        return new JLabel(text);
    }

    public static JTextArea createTextArea(int rows, int columns) {
        return new JTextArea(rows, columns);
    }

    public static JScrollPane createScrollPane(JTextArea textArea) {
        return new JScrollPane(textArea);
    }
}

// Adapter Pattern: Adapter for different exchange rate APIs
class ExchangeRateAdapter {
    private static final String API_URL = "https://openexchangerates.org/api/latest.json?app_id=YOUR_API_KEY";

    public Map<String, Double> fetchExchangeRates() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject jsonObject = new JSONObject(response.toString());
        JSONObject rates = jsonObject.getJSONObject("rates");
        Map<String, Double> exchangeRates = new HashMap<>();
        for (String key : rates.keySet()) {
            exchangeRates.put(key, rates.getDouble(key));
        }
        return exchangeRates;
    }
}

// Observer Pattern: Observer interface and subject class
interface Observer {
    void update(String conversion);
}

class ConversionHistory implements Observer {
    private JTextArea historyArea;

    public ConversionHistory(JTextArea historyArea) {
        this.historyArea = historyArea;
    }

    @Override
    public void update(String conversion) {
        historyArea.append(conversion + "\n");
    }
}

class CurrencyConverterSubject {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(String conversion) {
        for (Observer observer : observers) {
            observer.update(conversion);
        }
    }
}

// Main Application
public class CurrencyConverter extends JFrame {
    private JComboBox<String> fromCurrency;
    private JComboBox<String> toCurrency;
    private JTextField amountField;
    private JLabel resultLabel;
    private JTextArea historyArea;
    private Map<String, Double> exchangeRates = new HashMap<>();
    private CurrencyConverterSubject subject = new CurrencyConverterSubject();
    private Logger logger = Logger.getLogger("CurrencyConverterLog");
    private static final String HISTORY_FILE = "conversion_history.txt";

    public CurrencyConverter() {
        // Set up logging
        try {
            FileHandler fh = new FileHandler("CurrencyConverter.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set up the frame
        setTitle("Currency Converter");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Fetch the exchange rates
        fetchExchangeRates();

        // Create components
        String[] currencies = {"USD", "EUR", "GBP", "INR", "JPY", "AUD", "CAD"};
        fromCurrency = ComponentFactory.createCurrencyComboBox(currencies);
        toCurrency = ComponentFactory.createCurrencyComboBox(currencies);
        amountField = ComponentFactory.createTextField(10);
        JButton convertButton = ComponentFactory.createButton("Convert");
        resultLabel = ComponentFactory.createLabel("Converted Amount: ");
        historyArea = ComponentFactory.createTextArea(10, 30);
        historyArea.setEditable(false);
        JScrollPane scrollPane = ComponentFactory.createScrollPane(historyArea);

        // Add observer for conversion history
        ConversionHistory historyObserver = new ConversionHistory(historyArea);
        subject.addObserver(historyObserver);

        // Add action listener to the convert button
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });

        // Set up the layout
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(ComponentFactory.createLabel("From:"), gbc);
        gbc.gridx = 1;
        add(fromCurrency, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(ComponentFactory.createLabel("To:"), gbc);
        gbc.gridx = 1;
        add(toCurrency, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(ComponentFactory.createLabel("Amount:"), gbc);
        gbc.gridx = 1;
        add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(convertButton, gbc);
        gbc.gridx = 1;
        add(resultLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        // Load conversion history
        loadConversionHistory();

        // Display the frame
        setVisible(true);
    }

    private void fetchExchangeRates() {
        ExchangeRateAdapter adapter = new ExchangeRateAdapter();
        try {
            exchangeRates = adapter.fetchExchangeRates();
        } catch (Exception e) {
            logger.severe("Error fetching exchange rates: " + e.getMessage());
            e.printStackTrace();
            // Fallback to default exchange rates in case of failure
            exchangeRates.put("USD", 1.0);
            exchangeRates.put("EUR", 0.85);
            exchangeRates.put("GBP", 0.75);
            exchangeRates.put("INR", 74.0);
            exchangeRates.put("JPY", 110.0);
            exchangeRates.put("AUD", 1.4);
            exchangeRates.put("CAD", 1.3);
        }
    }

    private void convertCurrency() {
        String from = (String) fromCurrency.getSelectedItem();
        String to = (String) toCurrency.getSelectedItem();
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Invalid amount. Please enter a number greater than zero.");
            logger.warning("Invalid amount entered: " + amountField.getText());
            return;
        }

        if (!exchangeRates.containsKey(from) || !exchangeRates.containsKey(to)) {
            resultLabel.setText("Conversion not supported.");
            logger.warning("Conversion not supported for: " + from + " to " + to);
            return;
        }

        double fromRate = exchangeRates.get(from);
        double toRate = exchangeRates.get(to);
        double convertedAmount = amount * (toRate / fromRate);
        String resultText = String.format("Converted Amount: %.2f %s", convertedAmount, to);
        resultLabel.setText(resultText);

        // Add to conversion history
        String historyEntry = String.format("%.2f %s -> %.2f %s", amount, from, convertedAmount, to);
        subject.notifyObservers(historyEntry);

        // Save conversion history
        saveConversionHistory(historyEntry);
    }

    private void loadConversionHistory() {
        try (BufferedReader br = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                historyArea.append(line + "\n");
            }
        } catch (IOException e) {
            logger.warning("Error loading conversion history: " + e.getMessage());
        }
    }

    private void saveConversionHistory(String historyEntry) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            bw.write(historyEntry);
            bw.newLine();
        } catch (IOException e) {
            logger.severe("Error saving conversion history: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CurrencyConverter();
            }
        });
    }
}
