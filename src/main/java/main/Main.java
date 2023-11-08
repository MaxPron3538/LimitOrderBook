package main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final String inputPath = "input.txt";
    private static final String outputPath = "output.txt";

    private static TreeMap<Long,Long> mapOfBid = new TreeMap<>();
    private static TreeMap<Long,Long> mapOfAsk = new TreeMap<>();


    public static void main(String[] args) throws IOException {

        //for(int i = 1;i < 1000000;i++){
            /*
            if(i % 500 == 0) {
                Files.write(Path.of(inputPath), ("q,size," + (int)(Math.random()*i) + "\n").getBytes(), StandardOpenOption.APPEND);
            }
            else if(i % 600 == 0) {
                Files.write(Path.of(inputPath), ("q,best_bid" + "\n").getBytes(), StandardOpenOption.APPEND);
            }
            else if(i % 350 == 0) {
                Files.write(Path.of(inputPath), ("o,sell,"+(int)(Math.random()*i) + "\n").getBytes(), StandardOpenOption.APPEND);
            }
            */
          /*if(i % 3 == 0) {
                Files.write(Path.of(inputPath), ("u," + (int)(Math.random()*i) + "," + (int)(Math.random()*i) + ",bid" + "\n").getBytes(), StandardOpenOption.APPEND);
            }else{
                Files.write(Path.of(inputPath),("u,"+i+","+i+",bid"+"\n").getBytes(),StandardOpenOption.APPEND);
            }
        }
           */

        long start = System.currentTimeMillis();

        BufferedReader reader = new BufferedReader(new FileReader(inputPath));
        String order;

        if(Files.notExists(Path.of(outputPath))) {
            Files.createFile(Path.of(outputPath));
        }

        while ((order = reader.readLine()) != null) {
            String[] partsOrder = order.split(",");

            switch (partsOrder[0]) {
                case "u" -> {
                    if (order.contains(TypeLimitOrder.ask.getType())) {
                        updateAsk(Long.parseLong(partsOrder[1]), Long.parseLong(partsOrder[2]));
                    }
                    if(order.contains(TypeLimitOrder.bid.getType())) {
                        updateBid(Long.parseLong(partsOrder[1]), Long.parseLong(partsOrder[2]));
                    }
                }
                case "q" -> {
                    if (order.contains(TypeQuery.bestAsk.getType()) && !mapOfAsk.isEmpty()) {
                        Map.Entry<Long,Long> entry = mapOfAsk.firstEntry();
                        String bestAsk = entry.getKey() + "," + entry.getValue();
                        Files.write(Path.of(outputPath),(bestAsk+"\n").getBytes(),StandardOpenOption.APPEND);
                    }
                    if (order.contains(TypeQuery.bestBid.getType()) && !mapOfBid.isEmpty()) {
                        Map.Entry<Long,Long> entry = mapOfBid.lastEntry();
                        String bestBid = entry.getKey() + "," + entry.getValue();
                        Files.write(Path.of(outputPath),(bestBid+"\n").getBytes(),StandardOpenOption.APPEND);
                    }
                    if(order.contains(TypeQuery.size.getType())) {
                        long size = findSize(Long.parseLong(partsOrder[2]));
                        Files.write(Path.of(outputPath), (size + "\n").getBytes(), StandardOpenOption.APPEND);
                    }
                }
                case "o" -> {
                    if (order.contains(TypeMarketOrder.sell.getType())) {
                        long size = Long.parseLong(partsOrder[2]);
                        sellOrder(size);
                    }
                    if (order.contains(TypeMarketOrder.buy.getType())){
                        long size = Long.parseLong(partsOrder[2]);
                        buyOrder(size);
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    public static void updateBid(long price, long size){

        if(!mapOfBid.isEmpty()) {
            if (price >= mapOfBid.lastKey() ) {
                mapOfBid.put(price,size);

                if (size == 0) {
                    mapOfBid.remove(price);
                }
                return;
            }
            if(price < mapOfBid.firstKey()){
                if(size > 0){
                    mapOfBid.put(price,size);
                }
                return;
            }
            mapOfBid.put(price, size);
        }else{
            if(size > 0) {
                mapOfBid.put(price, size);
            }
        }
    }

    public static void updateAsk(long price, long size) {

        if(!mapOfAsk.isEmpty()) {
            if (price <= mapOfAsk.firstKey()) {
                mapOfAsk.put(price,size);

                if (size == 0) {
                    mapOfAsk.remove(price);
                }
                return;
            }
            if(price > mapOfAsk.lastKey()){
                if(size > 0){
                    mapOfAsk.put(price,size);
                }
                return;
            }
            mapOfAsk.put(price, size);
        }else{
            if(size > 0) {
                mapOfAsk.put(price, size);
            }
        }
    }

    public static long findSize(long price){

        if(mapOfAsk.containsKey(price)){
            return mapOfAsk.get(price);
        }
        if(mapOfBid.containsKey(price)){
            return mapOfBid.get(price);
        }
        return 0;
    }

    public static void sellOrder(long size){
        long remain;
        Map.Entry<Long,Long> lastBid = mapOfBid.lastEntry();

        while (size >= 0 && mapOfBid.size() > 0) {
            remain = size;
            size = size - lastBid.getValue();

            if(size < 0){
                remain = lastBid.getValue() - remain;
                mapOfBid.put(lastBid.getKey(),remain);
                return;
            }
            mapOfBid.remove(lastBid.getKey());
            lastBid = mapOfBid.lastEntry();
        }
        if(size < 0){
            remain = lastBid.getValue() - size;
            mapOfBid.put(lastBid.getKey(),remain);
        }
    }

    public static void buyOrder(long size) {
        long remain;
        Map.Entry<Long,Long> firstAsk = mapOfAsk.firstEntry();

        while (size >= 0 && mapOfAsk.size() > 0) {
            remain = size;
            size = size - firstAsk.getValue();

            if (size < 0) {
                remain = firstAsk.getValue() - remain;
                mapOfAsk.put(firstAsk.getKey(),remain);
                return;
            }
            mapOfAsk.remove(firstAsk.getKey());
            firstAsk = mapOfAsk.firstEntry();
        }
        if (size < 0) {
            remain = firstAsk.getValue() - size;
            mapOfAsk.put(firstAsk.getKey(),remain);
        }
    }
}


