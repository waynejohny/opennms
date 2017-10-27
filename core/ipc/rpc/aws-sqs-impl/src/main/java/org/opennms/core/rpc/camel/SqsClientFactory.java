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

package org.opennms.core.rpc.camel;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.ClientConfigurationFactory;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.beans.factory.FactoryBean;

import java.util.Properties;

public class SqsClientFactory implements FactoryBean<AmazonSQS> {

    /** The Constant AWS_REGION. */
    public static final String AWS_REGION = "aws_region";

    /** The Constant AWS_ACCESS_KEY_ID. */
    public static final String AWS_ACCESS_KEY_ID = "aws_access_key_id";

    /** The Constant AWS_SECRET_ACCESS_KEY. */
    public static final String AWS_SECRET_ACCESS_KEY = "aws_secret_access_key";

    /** The Constant AWS_QUEUE_NAME_PREFIX. */
    public static final String AWS_QUEUE_NAME_PREFIX = "aws_queue_name_prefix";

    /** The Constant AWS_USE_FIFO_QUEUE. */
    public static final String AWS_USE_FIFO_QUEUE = "aws_use_fifo_queue";

    @Override
    public AmazonSQS getObject() throws Exception {
        Properties awsConfig = new Properties();

        AWSCredentialsProvider credentialProvider;
        if (awsConfig.containsKey(AWS_ACCESS_KEY_ID) && awsConfig.containsKey(AWS_SECRET_ACCESS_KEY)) {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsConfig.getProperty(AWS_ACCESS_KEY_ID), awsConfig.getProperty(AWS_SECRET_ACCESS_KEY));
            credentialProvider = new AWSStaticCredentialsProvider(awsCreds);
        } else {
            credentialProvider = new ProfileCredentialsProvider();
        }
        // HACK: Disable HTTPS
        // Currently getting:
        //   InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty
        final ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        return AmazonSQSClientBuilder.standard()
                .withRegion(awsConfig.getProperty(AWS_REGION, Regions.CA_CENTRAL_1.getName()))
                .withCredentials(credentialProvider)
                .withClientConfiguration(clientConfig)
                .build();
    }

    @Override
    public Class<?> getObjectType() {
        return AmazonSQS.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
