package com.jiring.jiringexam;

import com.jiring.jiringexam.conroller.UserController;
import com.jiring.jiringexam.dto.UserIn;
import com.jiring.jiringexam.dto.UserPriority;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Time;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest
class JiringExamApplicationTests {
	@Autowired
	private UserController userController;
	@Test
	void contextLoads() {

	}
	@Test
	public void testConcurrentRequests() throws InterruptedException {
		int numRequests = 6; // Total requests to simulate
		CountDownLatch latch = new CountDownLatch(numRequests);

		// Simulate concurrent requests
		for (int i = 0; i < numRequests; i++) {
			new Thread(() -> {
				UserIn userIn = new UserIn();
				userIn.setName("hesamTest" + LocalTime.now().getNano());
				userIn.setPassword(String.valueOf(LocalTime.now().getNano()));
				userIn.setPriority(UserPriority.LOW);
				userController.signUp(userIn); // Simulate regular users
				latch.countDown();
			}).start();
		}

		latch.await(); // Wait for all requests to complete
	}
	@Test
	public void testHighPriorityUsersBypassQueue() throws InterruptedException {
		int numRequests = 6; // Total requests to simulate
		CountDownLatch latch = new CountDownLatch(numRequests);

		// Simulate concurrent requests for high-priority users
		for (int i = 0; i < numRequests; i++) {
			new Thread(() -> {
				UserIn userIn = new UserIn();
				userIn.setName("hesamTest" + LocalTime.now().getNano());
				userIn.setPassword("1233456" + LocalTime.now().getNano());
				userIn.setPriority(UserPriority.HIGH);
				userController.signUp(userIn); // Simulate high-priority users
				latch.countDown();
			}).start();
		}

		latch.await(); // Wait for all requests to complete
		// Assertions to check if high-priority users bypassed the queue
	}
	@Test
	public void testRegularUsersQueuing() throws InterruptedException {
		int numRequests = 6; // Total requests to simulate
		CountDownLatch latch = new CountDownLatch(numRequests);

		// Simulate concurrent requests for regular users
		for (int i = 0; i < numRequests; i++) {
			int finalI = i;
			new Thread(() -> {
				UserIn userIn = new UserIn();
				userIn.setName("hesamTest" + finalI);
				userIn.setPassword("1233456" + LocalTime.now().getSecond());
				userIn.setPriority(UserPriority.LOW);
				userController.signUp(userIn); // Simulate regular-priority users
				latch.countDown();
			}).start();
		}

		latch.await(); // Wait for all requests to complete
		// Assertions to check if regular users were queued and processed orderly
	}
	@Test
	public void testMixedPriorityUsers() throws InterruptedException {
		int numRequests = 46; // Total requests to simulate
		CountDownLatch latch = new CountDownLatch(numRequests);

		// Simulate concurrent requests for both high-priority and regular users
		for (int i = 0; i < numRequests; i++) {
			UserPriority priority = i % 2 == 0 ? UserPriority.HIGH : UserPriority.LOW;
			UserIn userIn = new UserIn();
			userIn.setName("hesamTest" + LocalTime.now().getNano());
			userIn.setPassword("1233456" + LocalTime.now().getNano());
			userIn.setPriority(priority);
			new Thread(() -> {
				userController.signUp(userIn); // Simulate mixed priority users
				latch.countDown();
			}).start();
			System.out.println("test number " + i);
		}

		latch.await(); // Wait for all requests to complete
		// Assertions to check if mixed priority users were processed correctly
	}


}

