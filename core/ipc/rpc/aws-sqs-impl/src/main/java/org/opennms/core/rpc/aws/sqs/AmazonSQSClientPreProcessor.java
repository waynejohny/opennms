/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2017-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.core.rpc.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import org.apache.camel.Exchange;
import org.opennms.core.camel.JmsQueueNameFactory;
import org.opennms.core.rpc.api.RpcRequest;
import org.opennms.core.rpc.api.RpcResponse;
import org.opennms.core.rpc.camel.CamelRpcClientPreProcessor;
import org.opennms.core.rpc.camel.CamelRpcConstants;
import org.opennms.core.rpc.camel.CamelRpcRequest;

public class AmazonSQSClientPreProcessor extends CamelRpcClientPreProcessor {

    @Override
    public void process(Exchange exchange) {

        @SuppressWarnings("unchecked")
        final CamelRpcRequest<RpcRequest,RpcResponse> wrapper = exchange.getIn().getBody(CamelRpcRequest.class);
        final JmsQueueNameFactory queueNameFactory = new JmsQueueNameFactory(CamelRpcConstants.JMS_QUEUE_PREFIX,
                wrapper.getModule().getId(), wrapper.getRequest().getLocation());
        final String queueName = queueNameFactory.getName().replaceAll("\\.", "-");
        final AmazonSQS sqs = exchange.getContext().getRegistry().findByType(AmazonSQS.class).iterator().next();
        try {
            sqs.createQueue(new CreateQueueRequest(queueName)).getQueueUrl();
        } catch (AmazonSQSException e) {
            if (e.getErrorCode().equals("QueueAlreadyExists")) {
                // pass
            } else {
                throw e;
            }
        }

        final String replyToQueueName = queueName + "-Reply";
        try {
            sqs.createQueue(new CreateQueueRequest(replyToQueueName)).getQueueUrl();
        } catch (AmazonSQSException e) {
            if (e.getErrorCode().equals("QueueAlreadyExists")) {
                // pass
            } else {
                throw e;
            }
        }

        exchange.getIn().setHeader(CamelRpcConstants.JMS_QUEUE_NAME_HEADER, queueName);
        exchange.getIn().setHeader("JmsReplyToQueueName", replyToQueueName);

        exchange.getIn().setHeader(CamelRpcConstants.CAMEL_JMS_REQUEST_TIMEOUT_HEADER, wrapper.getRequest().getTimeToLiveMs() != null ? wrapper.getRequest().getTimeToLiveMs() : CAMEL_JMS_REQUEST_TIMEOUT);
        if (wrapper.getRequest().getSystemId() != null) {
            exchange.getIn().setHeader(CamelRpcConstants.JMS_SYSTEM_ID_HEADER, wrapper.getRequest().getSystemId());
        }
        final String request = wrapper.getModule().marshalRequest((RpcRequest)wrapper.getRequest());
        exchange.getIn().setBody(request);
    }
}
