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
import org.apache.camel.AsyncCallback;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsEndpoint;
import org.opennms.core.camel.JmsQueueNameFactory;
import org.opennms.core.rpc.api.RpcModule;
import org.opennms.core.rpc.api.RpcRequest;
import org.opennms.core.rpc.api.RpcResponse;
import org.opennms.core.rpc.camel.CamelRpcConstants;
import org.opennms.core.rpc.camel.CamelRpcServerProcessor;
import org.opennms.core.rpc.camel.CamelRpcServerRouteManager;
import org.opennms.minion.core.api.MinionIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmazonSQSServerRouteManager extends CamelRpcServerRouteManager {
    private static final Logger LOG = LoggerFactory.getLogger(CamelRpcServerRouteManager.class);

    public AmazonSQSServerRouteManager(CamelContext context, MinionIdentity identity) {
        super(context, identity);
    }

    @Override
    public RouteBuilder getRouteBuilder(CamelContext context, MinionIdentity identity, RpcModule<RpcRequest, RpcResponse> module) {
        return new DynamicRpcRouteBuilder(context, identity, module);
    }

    private static final class DynamicRpcRouteBuilder extends RouteBuilder {
        private final MinionIdentity identity;
        private final RpcModule<RpcRequest,RpcResponse> module;
        private final JmsQueueNameFactory queueNameFactory;

        private DynamicRpcRouteBuilder(CamelContext context, MinionIdentity identity, RpcModule<RpcRequest,RpcResponse> module) {
            super(context);
            this.identity = identity;
            this.module = module;
            this.queueNameFactory = new JmsQueueNameFactory(CamelRpcConstants.JMS_QUEUE_PREFIX,
                    module.getId(), identity.getLocation());
        }

        public String getQueueName() {
            return queueNameFactory.getName();
        }

        @Override
        public void configure() throws Exception {
            final String queueName = queueNameFactory.getName().replaceAll("\\.", "-");
            final AmazonSQS sqs = getContext().getRegistry().findByType(AmazonSQS.class).iterator().next();
            try {
                System.err.println(queueName);
                sqs.createQueue(new CreateQueueRequest(queueName)).getQueueUrl();
            } catch (AmazonSQSException e) {
                if (e.getErrorCode().equals("QueueAlreadyExists")) {
                    // pass
                } else {
                    throw e;
                }
            }

            final JmsEndpoint endpoint = getContext().getEndpoint(String.format("jms:queue:%s?connectionFactory=#connectionFactory&correlationProperty=abc&acknowledgementModeName=CLIENT_ACKNOWLEDGE",
                    queueName), JmsEndpoint.class);

            from(endpoint).setExchangePattern(ExchangePattern.InOut)
                    .process(new AsyncProcessor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            throw new UnsupportedOperationException("This processor must be invoked using the async interface.");
                        }

                        @Override
                        public boolean process(Exchange exchange, AsyncCallback callback) {
                            if (!matches(exchange)) {
                                exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
                                exchange.setException(new Exception("Filtered"));
                                exchange.getOut().setFault(true);
                            }
                            callback.done(false);
                            return false;
                        }

                        boolean matches(Exchange exchange) {
                            final String systemId = (String)exchange.getIn().getHeader(CamelRpcConstants.JMS_SYSTEM_ID_HEADER);
                            if (systemId == null) {
                                return true;
                            }
                            return identity.getId().equals(systemId);
                        }
                    })
                    .process(new CamelRpcServerProcessor(module))
                    .routeId(getRouteId(module));
        }
    }
}
