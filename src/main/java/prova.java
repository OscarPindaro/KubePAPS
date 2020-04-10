import com.google.protobuf.Descriptors;
import com.squareup.okhttp.Response;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.AppsApi;
import io.kubernetes.client.apis.AppsV1Api;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.proto.Meta;
import io.kubernetes.client.proto.V1beta1Apiextensions;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import kubernetesApiWrapper.KubeApi;

import java.io.FileReader;

public class prova {

    public static void main(String[] args)  throws Exception{
        KubeApi.setUpApi("/home/oscar/.kube/config");
        System.out.println(KubeApi.getNodeList());
    }

    public static void proveVarie(String[] args) throws Exception{

        // file path to your KubeConfig
        String kubeConfigPath = "/home/oscar/.kube/config";

        // loading the out-of-cluster config, a kubeconfig from file-system
        ApiClient client =
                ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();


        // set the global default api-client to the in-cluster one from above
        Configuration.setDefaultApiClient(client);

        // the CoreV1Api loads default api-client from global configuration.
        CoreV1Api api = new CoreV1Api();


        /*
        // invokes the CoreV1Api client
        V1Pod pod = null;
        V1PodList list =
                api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
            if(item.getMetadata().getName().contains("hello-minikube"))
                pod = item;
        }


        System.out.println(pod.getMetadata().getName());
        //V1Status status = api.deleteNamespacedPod(pod.getMetadata().getName(), pod.getMetadata().getNamespace(), null, null,
        //                    null, null, null, null);

        final Response response = api.deleteNamespacedPodCall(pod.getMetadata().getName(), pod.getMetadata().getNamespace(),null, null, null, null, null, null, null, null).execute();

        if (!response.isSuccessful()) {
            System.out.println("Couldn't delete pod [{}] with reason: {}" + pod.getMetadata().getName() + response.message());
        }


         */

        V1NodeList  nodeList = api.listNode(null, null, null, null,
                                            null, null, null, null,
                                            null);

        //System.out.println(nodeList);



        AppsV1Api api2 = new AppsV1Api();


        V1DeploymentList i = api2.listDeploymentForAllNamespaces(null, null, null, null, null,
                                            null, null, null, null);

        V1Deployment myDep = null;
        for(V1Deployment dep : i.getItems()){
            System.out.println(dep.getMetadata().getName());
            if(dep.getMetadata().getName().contains("hello-minikube"))
                myDep = dep;

        }

        System.out.println(myDep.getSpec().getReplicas());

        myDep.getSpec().setReplicas(1);
        api2.replaceNamespacedDeployment(myDep.getMetadata().getName(), myDep.getMetadata().getNamespace(), myDep, null
                                        ,null );



        V1ReplicaSet replicaSet = null;
        for(V1ReplicaSet replica : api2.listNamespacedReplicaSet("default", null , null,
                                                                null, null, null, null
                                                                , null, null, null ).getItems())
        {
            api2.replaceNamespacedReplicaSet(replica.getMetadata().getName(), "default", replica, null, null);
        }

    }
}
