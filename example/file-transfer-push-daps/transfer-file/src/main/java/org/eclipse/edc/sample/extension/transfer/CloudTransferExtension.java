/*
 *  Copyright (c) 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - Initial implementation
 *
 */

package org.eclipse.edc.sample.extension.transfer;

import org.eclipse.edc.connector.contract.spi.offer.store.ContractDefinitionStore;
import org.eclipse.edc.connector.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.connector.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.policy.spi.store.PolicyDefinitionStore;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.asset.AssetIndex;
import org.eclipse.edc.spi.asset.AssetSelectorExpression;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;


public class CloudTransferExtension implements ServiceExtension {
    @Inject
    private AssetIndex assetIndex;
    @Inject
    private PolicyDefinitionStore policyDefinitionStore;
    @Inject
    private ContractDefinitionStore contractDefinitionStore;

    @Setting
    private static final String IONOS_BUCKET_NAME = "edc.file.transfer.bucket.name";

    @Setting
    private static final String IONOS_BLOB_NAME = "edc.file.transfer.blob.name";

    @Override
    public String name() {
        return "Cloud-Based Transfer";
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var policy = createPolicy();
        policyDefinitionStore.save(policy);

        var bucketName = context.getSetting(IONOS_BUCKET_NAME, IONOS_BUCKET_NAME);
        var blobName = context.getSetting(IONOS_BLOB_NAME, IONOS_BLOB_NAME);

        registerDataEntries(bucketName, blobName);
        registerContractDefinition(policy.getUid());
    }

    public void registerDataEntries(String bucketName, String blobName) {
        try {
            var asset = Asset.Builder.newInstance().id("1").build();
            var dataAddress = DataAddress.Builder.newInstance().type("IonosS3")
                    .property("storage", "s3-eu-central-1.ionoscloud.com")
                    .property("bucketName", bucketName)
                    .property("container", bucketName)
                    .property("blobName", blobName)
                    .keyName(blobName).build();

            assetIndex.accept(asset, dataAddress);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
        }
    }

    public void registerContractDefinition(String policyId) {
        var contractDefinition1 = ContractDefinition.Builder.newInstance().id("1").accessPolicyId(policyId)
                .contractPolicyId(policyId)
                .selectorExpression(
                        AssetSelectorExpression.Builder.newInstance().whenEquals(Asset.PROPERTY_ID, "1").build())
                .validity(31536000).build();

        contractDefinitionStore.save(contractDefinition1);
    }

    private PolicyDefinition createPolicy() {
        var usePermission = Permission.Builder.newInstance().action(Action.Builder.newInstance().type("USE").build())
                .build();

        return PolicyDefinition.Builder.newInstance()
                .policy(Policy.Builder.newInstance().permission(usePermission).build()).build();
    }
}
