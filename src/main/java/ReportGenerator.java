import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ReportGenerator {

    static class Product {
        private final int productID;
        private final String productName;
        private final double price;

        public Product(int productID, String productName, double price) {
            this.productID = productID;
            this.productName = productName;
            this.price = price;
        }

        public int getProductID() {
            return productID;
        }

        public String getProductName() {
            return productName;
        }

        public double getPrice() {
            return price;
        }
    }

    static class OrderProcessor implements Runnable {
        private final String filePath;
        private double totalCost;
        private int totalAmount;
        private int totalDiscount;
        private int totalLines;
        private Product mostExpensiveProduct;
        private double highestCostAfterDiscount;

        public OrderProcessor(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length != 3) continue;

                    int productId, amount, discount;
                    try {
                        productId = Integer.parseInt(parts[0].trim());
                        amount = Integer.parseInt(parts[1].trim());
                        discount = Integer.parseInt(parts[2].trim());
                    } catch (NumberFormatException e) {
                        continue; // skip invalid line
                    }

                    Product product = findProductById(productId);
                    if (product == null) continue;

                    double costBeforeDiscount = product.getPrice() * amount;
                    double costAfterDiscount = costBeforeDiscount - discount;

                    totalCost += costAfterDiscount;
                    totalAmount += amount;
                    totalDiscount += discount;
                    totalLines++;

                    if (costAfterDiscount > highestCostAfterDiscount) {
                        highestCostAfterDiscount = costAfterDiscount;
                        mostExpensiveProduct = product;
                    }
                }
            } catch (IOException e) {
                System.out.println("Failed to read file " + filePath + ": " + e.getMessage());
            }
        }

        private Product findProductById(int id) {
            return productCatalogMap.get(id);
        }

        public void printReport() {
            System.out.println("Report for: " + filePath);
            System.out.printf("Total cost: $%.2f\n", totalCost);
            System.out.println("Total items bought: " + totalAmount);
            double avgDiscount = totalLines > 0 ? (double) totalDiscount / totalLines : 0;
            System.out.printf("Average discount per line: $%.2f\n", avgDiscount);
            if (mostExpensiveProduct != null) {
                System.out.printf("Most expensive purchase after discount: %s ($%.2f)\n",
                        mostExpensiveProduct.getProductName(), highestCostAfterDiscount);
            }
            System.out.println("---------------------------------------------------");
        }
    }

    private static final String[] ORDER_FILES = {
            "src\\main\\resources/2021_order_details.txt",
            "src\\main\\resources/2022_order_details.txt",
            "src\\main\\resources/2023_order_details.txt",
            "src\\main\\resources/2024_order_details.txt"
    };

    static Map<Integer, Product> productCatalogMap = new HashMap<>();

    public static void loadProducts(String productFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(productFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 3) continue;

                int id;
                double price;
                try {
                    id = Integer.parseInt(parts[0].trim());
                    price = Double.parseDouble(parts[2].trim());
                } catch (NumberFormatException e) {
                    continue; // skip invalid line
                }

                String name = parts[1].trim();
                productCatalogMap.put(id, new Product(id, name, price));
            }
        }
    }

    public static void main(String[] args) {
        try {
            loadProducts("src\\main\\resources\\Products.txt");
        } catch (IOException e) {
            System.out.println("Error loading product catalog: " + e.getMessage());
            return;
        }

        List<Thread> threads = new ArrayList<>();
        List<OrderProcessor> processors = new ArrayList<>();

        for (String filePath : ORDER_FILES) {
            OrderProcessor processor = new OrderProcessor(filePath);
            Thread thread = new Thread(processor);
            threads.add(thread);
            processors.add(processor);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }

        for (OrderProcessor processor : processors) {
            processor.printReport();
        }
    }
}
