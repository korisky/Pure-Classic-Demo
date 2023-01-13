package com.own.spring.demo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Roylic
 * 2023/1/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputParam {
    private String name;
    private Integer id;
}
