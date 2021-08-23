package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;
import org.openqa.selenium.remote.http.HttpResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private RestTemplate restTemplate;
    private Scanner scanner = new Scanner(System.in);


	public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }



    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.restTemplate = new RestTemplate();
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				System.out.println("Your current balance is : $" + viewCurrentBalance());
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
				askForTransferDetails();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
				approvedOrRejectTransfer();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private BigDecimal viewCurrentBalance() {
		// TODO Auto-generated method stub

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity(httpHeaders);
		Balance balance = restTemplate.exchange(API_BASE_URL + "balance", HttpMethod.GET,
				entity, Balance.class).getBody();

		//System.out.println("Your current account balance is: $" + balance.getBalance());
		return balance.getBalance();
	}



	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity(httpHeaders);
		String result = restTemplate.exchange(API_BASE_URL + "history", HttpMethod.GET,entity,String.class).getBody();

		System.out.println(result);


	}

	private void askForTransferDetails(){
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity(httpHeaders);
		Scanner scanner = new Scanner(System.in);
		System.out.print("\nPlease enter transfer ID to view details (0 to cancel): ");
		boolean isLooping = true;
		while(isLooping) {
		int input = Integer.parseInt(scanner.nextLine());
		if (input == 0){
			mainMenu();
		}
		else {
				try {
					String result = restTemplate.exchange(API_BASE_URL + "details/" + input, HttpMethod.GET, entity, String.class).getBody();
					System.out.println(result);
					isLooping = false;
				} catch (RestClientResponseException e) {
					System.out.print("\nTransfer ID not valid.  Please try again (0 to exit): ");
				}
			}
		}
	}


	private void viewPendingRequests() {
		// TODO Auto-generated method stub

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity(httpHeaders);
		String result = restTemplate.exchange(API_BASE_URL + "requests", HttpMethod.GET,entity,String.class).getBody();

		System.out.println(result);

	}

//	private boolean isTransfer() {
//		HttpHeaders httpHeaders = new HttpHeaders();
//		httpHeaders.setBearerAuth(currentUser.getToken());
//
//	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity(httpHeaders);
		/*List<User> userList = restTemplate.exchange("http://localhost:8080/userlist", HttpMethod.GET,
				entity, new ParameterizedTypeReference<List<User>>() {}).getBody();*/
		User[] userArray = restTemplate.exchange(API_BASE_URL + "userlist", HttpMethod.GET,
				entity, User[].class).getBody();
		List<User> userList = new ArrayList<User> (Arrays.asList(userArray));

		userList.removeIf(user -> user.getUsername().equals(currentUser.getUser().getUsername()));

		System.out.println("------------------------------");
		System.out.println("Users                         ");
		System.out.println("ID                        Name");
		System.out.println("------------------------------");
		for(User users: userList) {
			System.out.println(users.getId() + "                     " + users.getUsername());
		}
		System.out.print("\nEnter ID of user you are sending to (enter 0 to cancel): ");
		int choice = Integer.parseInt(scanner.nextLine());
		List<Integer> idList = new ArrayList<>();
		for(User user: userList){
			idList.add(user.getId());
		}
		boolean isLooping = false;
		while (!isLooping) {
			if (choice == 0) {
				mainMenu();
			}
			else if(idList.contains(choice)){
				isLooping = true;
			}
			else{
				System.out.print("\nInvalid User ID try again (enter 0 to exit):  ");
				choice = Integer.parseInt(scanner.nextLine());
			}
		}
		System.out.print("\nEnter amount to transfer: ");
		BigDecimal transferAmount = null;
		while(isLooping) {
			try {
				transferAmount = new BigDecimal(scanner.nextLine());
				BigDecimal balance = viewCurrentBalance();
				if (transferAmount.compareTo(balance)>0) {
					System.out.print("\nYou have insufficient funds. Enter a valid amount: ");

				}
				else {
					Transfer transfer = new Transfer(2,2,
							currentUser.getUser().getId(), choice, transferAmount);
					//System.out.println(transfer.getAccount_from() + " " + transfer.getAccount_to() +" " + transfer.getTransferStatusID() + " " + transfer.getTransferTypeID() + " " + transfer.getAmount());
					System.out.println(transfer.getTransfer_status_ID() + " " + transfer.getTransfer_type_ID());
					HttpHeaders httpHeader1 = new HttpHeaders();
					httpHeader1.setContentType(MediaType.APPLICATION_JSON);
					httpHeader1.setBearerAuth(currentUser.getToken());
					HttpEntity<Transfer> entity1 = new HttpEntity<>(transfer,httpHeader1);
					//System.out.println(entity1.getBody().getAccount_to());
					transfer = restTemplate.postForObject(API_BASE_URL + "transfer", entity1, Transfer.class);
					isLooping = false;
				}
			} catch (NumberFormatException e) {
				System.out.print("\nPlease enter a valid amount: ");
			}
		}
		System.out.println("Transfer successful");

	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity(httpHeaders);
		/*List<User> userList = restTemplate.exchange("http://localhost:8080/userlist", HttpMethod.GET,
				entity, new ParameterizedTypeReference<List<User>>() {}).getBody();*/
		User[] userArray = restTemplate.exchange(API_BASE_URL + "userlist", HttpMethod.GET,
				entity, User[].class).getBody();
		List<User> userList = new ArrayList<User> (Arrays.asList(userArray));

		userList.removeIf(user -> user.getUsername().equals(currentUser.getUser().getUsername()));

		System.out.println("------------------------------");
		System.out.println("Users                         ");
		System.out.println("ID                        Name");
		System.out.println("------------------------------");
		for(User users: userList) {
			System.out.println(users.getId() + "                     " + users.getUsername());
		}
		System.out.print("\nEnter ID of user you are requesting from (enter 0 to cancel): ");
		int choice = Integer.parseInt(scanner.nextLine());
		List<Integer> idList = new ArrayList<>();
		for(User user: userList){
			idList.add(user.getId());
		}
		boolean isLooping = false;
		while (!isLooping) {
			if (choice == 0) {
				mainMenu();
			}
			else if(idList.contains(choice)){
				isLooping = true;
			}
			else{
				System.out.print("\nInvalid User ID try again (enter 0 to exit):  ");
				choice = Integer.parseInt(scanner.nextLine());
			}
		}
		System.out.print("\nEnter requested amount: ");

		BigDecimal transferAmount = null;
		while(isLooping) {
			try {
				transferAmount = new BigDecimal(scanner.nextLine());


				 {
					Transfer transfer = new Transfer(1,1,
							currentUser.getUser().getId(), choice, transferAmount);
					//System.out.println(transfer.getAccount_from() + " " + transfer.getAccount_to() +" " + transfer.getTransferStatusID() + " " + transfer.getTransferTypeID() + " " + transfer.getAmount());

					HttpHeaders httpHeader1 = new HttpHeaders();
					httpHeader1.setContentType(MediaType.APPLICATION_JSON);
					httpHeader1.setBearerAuth(currentUser.getToken());
					HttpEntity<Transfer> entity1 = new HttpEntity<>(transfer,httpHeader1);
					//System.out.println(entity1.getBody().getAccount_to());
					transfer = restTemplate.postForObject(API_BASE_URL + "transfer", entity1, Transfer.class);
					isLooping = false;
				}
			} catch (NumberFormatException e) {
				System.out.print("\nPlease enter a valid amount: ");
			}
		}
		System.out.println("Request sent successfully");

	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private void approvedOrRejectTransfer() {
		System.out.print("\nPlease enter the transfer ID to approve/reject (0 to cancel): ");
		int id = Integer.parseInt(scanner.nextLine());

		boolean isIdValid = true;
		while(isIdValid) {
			if(id == 0) {
				mainMenu();
			}else {
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setBearerAuth(currentUser.getToken());
				HttpEntity<Integer> entity1 = new HttpEntity<>(id,httpHeaders);
				boolean pending =restTemplate.postForObject(API_BASE_URL + "pending",
						entity1, boolean.class);
				if(pending) {
					isIdValid = false;
				}else {
					System.out.print("\nInvalid Transfer ID..Please try again: ");
					 id = Integer.parseInt(scanner.nextLine());
				}
			}

		}



		System.out.print("\n1: Approve\n2: Reject\n0: Don't approve or reject\n-----------");
		System.out.print("\nPlease choose an option: ");
		int input = Integer.parseInt(scanner.nextLine());

		boolean isLooping = true;
		while(isLooping){
			if(input == 0) {
				mainMenu();
			}else if(input == 1) {
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setBearerAuth(currentUser.getToken());
				HttpEntity<Integer> entity1 = new HttpEntity<>(id,httpHeaders);
				String approve =restTemplate.postForObject(API_BASE_URL + "approve",
						entity1, String.class);
				System.out.println(approve);
				isLooping = false;
			}else if(input == 2) {
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.setBearerAuth(currentUser.getToken());
				HttpEntity<Integer> entity1 = new HttpEntity<>(id,httpHeaders);
				String reject =restTemplate.postForObject(API_BASE_URL + "reject",
						entity1, String.class);
				System.out.println(reject);
				isLooping = false;
			}else {
				System.out.print("\nInvalid option..Please try again: ");
				input = Integer.parseInt(scanner.nextLine());
			}
		}




	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
