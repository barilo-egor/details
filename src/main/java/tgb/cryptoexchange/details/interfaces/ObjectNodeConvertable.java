package tgb.cryptoexchange.details.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.function.Function;

@FunctionalInterface
public interface ObjectNodeConvertable<T> {

    Function<T, ObjectNode> mapFunction();
}
