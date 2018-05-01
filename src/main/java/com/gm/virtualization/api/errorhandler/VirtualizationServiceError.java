package com.gm.virtualization.api.errorhandler;

import com.gm.virtualization.api.VirtualizationServiceApi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Representation of any error in {@link VirtualizationServiceApi}
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VirtualizationServiceError implements Serializable {

    private String errorMessage;
}
