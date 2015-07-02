package org.uma.jmetal.parameter.impl;

import org.uma.jmetal.parameter.Parameter;
import org.uma.jmetal.parameter.space.ParameterSpace;
import org.uma.jmetal.parameter.space.ParameterSpaceFactory;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;

/**
 * {@link SimpleParameter} is a basic implementation of {@link Parameter}. It
 * provides a basic support for the most generic properties required by this
 * interface.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 */
public class SimpleParameter<Value> extends SimpleDescribedEntity implements
		Parameter<Value> {

	/**
	 * The current {@link Value} of this {@link Parameter}.
	 */
	private Value value;
	/**
	 * The {@link ParameterSpace} which tells which values are compatible.
	 */
	private ParameterSpace<Value> space = new ParameterSpaceFactory()
			.createFullSpace(true);

	/**
	 * Create a {@link SimpleParameter} with a given name and a given
	 * description.
	 * 
	 * @param name
	 *            the name of the {@link Parameter}
	 * @param description
	 *            the description of the {@link Parameter}
	 */
	public SimpleParameter(String name, String description) {
		super(name, description);
	}

	/**
	 * Create a {@link SimpleParameter} with a given name and a
	 * <code>null</code> description.
	 * 
	 * @param name
	 *            the name of the {@link Parameter}
	 */
	public SimpleParameter(String name) {
		super(name);
	}

	/**
	 * Create a {@link SimpleParameter} with the class name as its name and a
	 * <code>null</code> description.
	 * 
	 */
	public SimpleParameter() {
		super();
	}

	@Override
	public void set(Value value) {
		if (!space.contains(value)) {
			throw new InvalidValueException(this, value);
		} else {
			this.value = value;
		}
	}

	@Override
	public Value get() {
		return value;
	}

	@Override
	public ParameterSpace<Value> getSpace() {
		return space;
	}

	public void setSpace(ParameterSpace<Value> space) {
		if (!space.contains(value)) {
			throw new InvalidValueException(this, value,
					"impossible to change the space from " + this.space
							+ " to " + space);
		} else {
			this.space = space;
		}
	}
}
