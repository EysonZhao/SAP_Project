package sme.perf.result.impl;

import java.util.*;
public interface IParseResult <T>{
	List<T> parse(String fileName) throws Exception;
}
