package com.zinkworks.atmservices.dto.admin;

import lombok.Data;

import java.util.Map;

@Data
public class LoadingMoneyRequest {
    Map<Integer,Integer> denominationMap;
}
