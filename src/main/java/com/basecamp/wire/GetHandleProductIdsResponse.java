package com.basecamp.wire;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class GetHandleProductIdsResponse implements Response {

    private final List<String> productIds;

}