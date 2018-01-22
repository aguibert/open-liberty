package bval.v20.customprovider;

import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;

public class MyCustomValidatorFactory implements ValidatorFactory {

    @Override
    public Validator getValidator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ValidatorContext usingContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessageInterpolator getMessageInterpolator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TraversableResolver getTraversableResolver() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConstraintValidatorFactory getConstraintValidatorFactory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ParameterNameProvider getParameterNameProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClockProvider getClockProvider() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> paramClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

}
