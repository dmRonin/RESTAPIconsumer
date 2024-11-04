import models.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class Consumer {

    private static final String URL = "http://94.198.50.185:7081/api/users";
    private static String sessionId;

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        List<User> users = getUsers(restTemplate);

        System.out.println("Начальное id сессси " + sessionId);

        String addUserResponse = addUser(restTemplate, 3L, "James", "Brown", (byte) 30);
        System.out.println(addUserResponse);
        String updateUserResponse = updateUser(restTemplate, 3L, "Thomas", "Shelby", (byte) 30);
        System.out.println(updateUserResponse);
        String deleteUserResponse = deleteUser(restTemplate, 3L);
        System.out.println(deleteUserResponse);
        String finalCode = addUserResponse + updateUserResponse + deleteUserResponse;
        System.out.println("Итог:\n" + finalCode + "Длина:" + finalCode.length());
        System.out.println("Конечное id сессси " + sessionId);

    }

    private static List<User> getUsers(RestTemplate restTemplate) {
        ResponseEntity<User[]> responseEntity = restTemplate.getForEntity(URL, User[].class);
        sessionId = responseEntity.getHeaders().getFirst("Set-Cookie");

        return Arrays.asList(responseEntity.getBody());
    }

    private static String addUser(RestTemplate restTemplate,
                                  Long id, String name, String lastName, Byte age) {
        User newUser = new User(id, name, lastName, age);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", sessionId);

        HttpEntity<User> requestEntity = new HttpEntity<>(newUser, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(URL, requestEntity, String.class);
        return response.getBody();
    }

    private static String updateUser(RestTemplate restTemplate, Long id, String name, String lastName, Byte age) {
        User updatedUser = new User(id, name, lastName, age);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", sessionId);

        HttpEntity<User> requestEntity = new HttpEntity<>(updatedUser, headers);
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.PUT, requestEntity, String.class);
        return response.getBody();
    }

    private static String deleteUser(RestTemplate restTemplate, Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionId);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(URL + "/" + id, HttpMethod.DELETE, requestEntity, String.class);
        return response.getBody();
    }
}
