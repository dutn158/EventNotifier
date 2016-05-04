import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;

public class EventService {

	private Vector<Object> vector;

	private static EventService sInstance;

	private EventService() {
		vector = new Vector<Object>();
	}

	public synchronized static EventService getInstance() {
		if (sInstance == null) {
			sInstance = new EventService();
		}
		return sInstance;
	}

	public void register(Object obj) {
		if (!vector.contains(obj)) {
			vector.addElement(obj);
		}
	}

	public void unregister(Object obj) {
		if (vector.contains(obj)) {
			vector.remove(obj);
		}
	}

	public void push(Object event) {
		String nameValue = progressNamed(event);
		for (Object obj : vector) {
			Class<?> cls = obj.getClass();
			Method[] methods = cls.getDeclaredMethods();

			for (Method method : methods) {
				Subscriber subscriber = method.getAnnotation(Subscriber.class);
				if (subscriber != null) {
					progressSubscriber(obj, method, nameValue, event);
				}
			}
		}
	}

	private String progressNamed(Object event) {
		Class<?> eventClass = event.getClass();
		Filter name = null;
		String value = null;

		if (eventClass.isAnnotationPresent(Filter.class)) {
			name = (Filter) eventClass.getAnnotation(Filter.class);
			value = name.value();
		}

		return value;
	}

	private void progressSubscriber(Object obj, Method method, String nameValue, Object event) {

		if (method.getParameterCount() > 1) {
			throw new IllegalArgumentException(String.format("Method %s must only have 1 parameter", method.getName()));
		} else if (method.getParameterCount() == 0) {
			throw new IllegalArgumentException(
					String.format("Method %s must have at least 1 parameter", method.getName()));
		}
		
		if (method.getModifiers() != Modifier.PUBLIC) {
			throw new IllegalArgumentException(String.format("Method %s at %s must be public", method.getName(), obj.getClass()));
		}

		if (method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
			
			try {
				method.invoke(obj, event);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}

	}

}
