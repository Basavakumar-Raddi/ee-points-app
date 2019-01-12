package com.eesti.energia.point.controller;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApiResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private T response;

	private boolean success = true;

	private String errorCode;

	private String message;

}
