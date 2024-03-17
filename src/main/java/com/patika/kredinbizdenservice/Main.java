package com.patika.kredinbizdenservice;

import com.patika.kredinbizdenservice.model.*;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static List<User> userList = new ArrayList<>();

    public static void main(String[] args) {
        User user1 = new User("Ahmet","Yılmaz", LocalDate.now(), "ahmet@gmail.com", "1234", "123456789", true);
        User user2 = new User("Mehmet","Yılmaz", LocalDate.now(), "cemdrman@gmail.com", "1233", "123456788", true);
        User newUser1 = createUser(user1);
        User newUser2 = createUser(user2);

        User mostApplicationsByUser = mostApplicationsByUser(userList);
        mostApplicationsByUser.toString();

        User biggestLoanByUser = biggestLoanByUser(userList);
        biggestLoanByUser.toString();

        List<Application> lastOneMonthApplications =getLastOneMonthApplications(userList);
        lastOneMonthApplications.toString();

        List <CreditCard> sortedList = sortCampaignsByCreditCardOffers(new Bank());
        sortedList.toString();

        List <Application> allApplicationsOfUser= allApplicationsOfUser("cemdrman@gmail.com");
        allApplicationsOfUser.toString();
    }

    public static User createUser(User user){
        for(User u : userList){
            if(u.getEmail().equals(user.getEmail())){
                System.out.println("Bu email ile kayıtlı kullanıcı bulunmaktadır.");
                return null;
            }
        }
        try {
            user.setPassword(hashSHA512(user.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        userList.add(user);

        return user;
    }

    public static String hashSHA512(String input) throws NoSuchAlgorithmException {
        // SHA-512 hasher oluştur
        MessageDigest md = MessageDigest.getInstance("SHA-512");

        // Parolayı byte dizisine dönüştür ve hasher'e güncelle
        md.update(input.getBytes());

        // Hash değerini hesapla
        byte[] digest = md.digest();

        // Byte dizisini 16'lık sayı sisteminde büyük bir sayıya dönüştür
        BigInteger bigInt = new BigInteger(1, digest);

        // 16'lık sayı sistemiyle string'e dönüştür
        String hashedPassword = bigInt.toString(16);

        // Eğer hash değeri 32 karakterden daha kısa ise başına sıfır ekle
        while (hashedPassword.length() < 128) {
            hashedPassword = "0" + hashedPassword;
        }
        return hashedPassword;
    }

    public static User mostApplicationsByUser(List<User> userList){
        int max = 0;
        User user = null;
        for(User u : userList){
            if(u.getApplicationList().size() > max){
                max = u.getApplicationList().size();
                user = u;
            }
        }
        return user;
    }

    public static User biggestLoanByUser(List<User> userList){
        int max = 0;
        User user = null;
        for(User u : userList){
            for(Application a : u.getApplicationList()){
                if(a.getLoan().getAmount().intValue() > max){
                    max = a.getLoan().getAmount().intValue();
                    user = u;
                }
            }
        }
        return user;
    }

    public static List<Application> getLastOneMonthApplications(List<User> userList){
        List<Application> applicationList = new ArrayList<>();
        for(User u : userList){
            for(Application a : u.getApplicationList()){
                if(a.getLocalDateTime().isAfter(LocalDate.now().minusMonths(1).atStartOfDay())){
                    applicationList.add(a);
                }
            }
        }
        return applicationList;
    }

    public static List<CreditCard> sortCampaignsByCreditCardOffers(Bank bank){
        Map<CreditCard, Integer> creditCardCampaignCount = new HashMap<>();
        for(CreditCard creditCard : bank.getCreditCards()){
            creditCardCampaignCount.put(creditCard, creditCard.getCampaignList().size());
        }

        List<Map.Entry<CreditCard, Integer>> list = new ArrayList<>(creditCardCampaignCount.entrySet());
        list.sort(Map.Entry.<CreditCard, Integer>comparingByValue().reversed());

        List<CreditCard> sortedList = new ArrayList<>();
        for(Map.Entry<CreditCard, Integer> entry : list){
            sortedList.add(entry.getKey());
        }

        return sortedList;
    }

    public static List<Application> allApplicationsOfUser(String mail){
        List<Application> applicationList = new ArrayList<>();
        for(User u : userList){
            if(u.getEmail().equals(mail)){
                applicationList = u.getApplicationList();
            }
        }
        return applicationList;
    }

}
