package com.basecamp.wire;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GetProductInfoResponse implements Response {

    private final String id;

    private final String name;

    private final String status;

}