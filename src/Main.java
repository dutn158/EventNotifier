
public class Main {

	public static void main(String[] args) {
		
		EventService.getInstance().register(new EventListener());

		EventService.getInstance().register(new EventListener2());

		EventService.getInstance().push(new City("Ho Chi Minh", "Viet Nam"));
		
		EventService.getInstance().push(new Street("Pham Ngu Lao, Q1"));

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException ie) {
					
				}
				EventService.getInstance().push(new City("Binh Phuoc", "Viet Nam"));
			}
		}).start();

	}

	private static class EventListener {

		@Subscriber
		public void onEvent(City city) {
			System.out.println("onEvent City => " + city.name + " " + city.country);
		}
		
		@Subscriber
		public void onEvent(Street street) {
			System.out.println("onEvent Street => " + street.name);
		}

	}

	private static class EventListener2 {

		@Subscriber
		public void onEvent2(City city) {
			System.out.println("onEvent2 " + city.name + " " + city.country);
		}

	}

}
