package view;

/**
 * Graphical representation of an object from the model.
 */
public abstract class ObjectView<T> implements ViewConstants {
	protected T object;

	public ObjectView(T object) {
		this.object = object;
	}

}
