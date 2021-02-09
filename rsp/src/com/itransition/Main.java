package com.itransition;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static java.lang.Math.random;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Base64.getEncoder;
import static java.util.Base64.getUrlEncoder;
import static javax.crypto.Mac.getInstance;

public class Main {

    public static void main(String[] args) throws Exception {
        validateCommandLineArguments(args);
        final Map<Integer, String> choiceMap = populateChoiceMap(args);
        final String secretKey = generateSecretKey();
        final Integer computerNumber = (int) (random() * args.length + 1);
        final String computerChoice = choiceMap.get(computerNumber);
        System.out.println("HMAC: " + encodeChoice(secretKey, computerChoice));
        Integer userChoice = showMenuAndGetUserChoice(args);
        System.out.println("Your choice: " + choiceMap.get(userChoice));
        System.out.println("Computer's choice: " + computerChoice);
        computeWinner(computerNumber, userChoice, args.length, secretKey);
        System.out.println("Secret key: " + secretKey);
        System.exit(1);
    }

    private static void computeWinner(Integer computerNumber, Integer userChoice, Integer argumentsLength, String secretKey) {
        int numberOfSuperiorElements = argumentsLength / 2;
        if (computerNumber.equals(userChoice)) {
            System.out.println("It's a draw!");
            System.out.println("Secret key: " + secretKey);
            System.exit(1);
        }
        int min = computerNumber < userChoice ? computerNumber : userChoice;
        int max = computerNumber > userChoice ? computerNumber : userChoice;
        Map<Integer, String> minMaxMap = new HashMap<>();
        minMaxMap.put(min, min == computerNumber ? "computer" : "you");
        minMaxMap.put(max, max == computerNumber ? "computer" : "you");
        if (min + numberOfSuperiorElements >= max) {
            System.out.println(minMaxMap.get(max) + " won!");
        } else {
            System.out.println(minMaxMap.get(min) + " won!");
        }
    }

    private static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static void validateCommandLineArguments(String[] input) {
        if (input.length < 3) {
            System.err.println("Wrong input! Number of elements must be greater or equal 3");
            System.exit(1);
        }
        if (input.length % 2 == 0) {
            System.err.println("Wrong input! Number of elements must be odd");
            System.exit(1);
        }
        Set<String> hashSet = new HashSet<>(asList(input));
        if (input.length != hashSet.size()) {
            System.err.println("Elements must be unique!");
            System.exit(1);
        }
    }

    private static String encodeChoice(String key, String data) throws Exception {
        Mac mac = getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        return getEncoder().encodeToString(mac.doFinal(data.getBytes(UTF_8)));
    }

    private static Integer showMenuAndGetUserChoice(String[] args) {
        System.out.println("Available moves:");
        for (int i = 1; i < args.length + 1; i++) {
            System.out.println(i + ". " + args[i - 1]);
        }
        System.out.println("0. Exit");
        System.out.println("Enter your move:");
        Scanner scan = new Scanner(System.in);
        String userChoice = scan.next();
        if (userChoice.equals("0")) {
            System.out.println("See you next time!");
            System.exit(1);
        }
        try {
            parseInt(userChoice);
        } catch (Exception e) {
            System.out.println("Please input number a number");
            showMenuAndGetUserChoice(args);
        }
        int numberChoice = parseInt(userChoice);
        if (numberChoice > args.length || numberChoice < 0) {
            System.out.println("Please input number from below");
            showMenuAndGetUserChoice(args);
        }
        return numberChoice;
    }

    private static Map<Integer, String> populateChoiceMap(String[] args) {
        final Map<Integer, String> choiceMap = new HashMap<>();
        for (int i = 1; i < args.length + 1; i++) {
            choiceMap.put(i, args[i - 1]);
        }
        return choiceMap;
    }
}
