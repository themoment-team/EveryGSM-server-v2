from diagrams import Diagram, Cluster, Edge
from diagrams.aws.compute import EC2
from diagrams.aws.devtools import Codedeploy
from diagrams.aws.network import InternetGateway
from diagrams.aws.storage import S3
from diagrams.onprem.client import User
from diagrams.onprem.network import Nginx
from diagrams.onprem.vcs import Github

graph_attr = {
    "fontsize": "16",
    "fontname": "Arial",
    "splines": "ortho",
    "nodesep": "0.8",
    "ranksep": "1.2",
    "pad": "0.8",
    "bgcolor": "#f8f9fa"
}

node_attr = {
    "fontsize": "12",
    "fontname": "Arial",
    "shape": "box",
    "style": "rounded,filled",
    "margin": "0.3,0.1"
}

edge_attr = {
    "fontsize": "9",
    "fontname": "Arial",
    "penwidth": "2",
    "labeldistance": "0.5",
    "labelangle": "0"
}

with Diagram(
        "EveryGSM Cloud Architecture",
        show=False,
        filename="everygsm-cloud-diagram",
        direction="TB",
        graph_attr=graph_attr,
        node_attr=node_attr,
        edge_attr=edge_attr,
):
    user = User("사용자")

    with Cluster("CI/CD Pipeline", graph_attr={"bgcolor": "#e3f2fd", "style": "rounded", "margin": "10"}):
        github = Github("GitHub\nRepository")
        s3 = S3("S3 Bucket")
        codedeploy = Codedeploy("CodeDeploy")

        github >> Edge(label="push", color="#2196f3") >> s3
        s3 >> Edge(label="artifact", color="#2196f3") >> codedeploy

    with Cluster("everygsm-vpc\n10.10.0.0/24", graph_attr={"bgcolor": "#f3e5f5", "style": "rounded", "margin": "20"}):

        igw = InternetGateway("everygsm-igw")

        with Cluster("everygsm-prod-public-2a\n10.10.0.64/26", graph_attr={"bgcolor": "#c8e6c9", "style": "rounded", "margin": "12"}):
            nat = EC2("everygsm-nat\nt4g.micro\n10.10.0.73")
            nginx = Nginx("nginx\n(Reverse Proxy)")

        with Cluster("everygsm-prod-private-2a\n10.10.0.0/26", graph_attr={"bgcolor": "#ffcdd2", "style": "rounded", "margin": "12"}):
            prod = EC2("everygsm-prod\nt3.micro\n10.10.0.60")

    # 사용자 트래픽 흐름
    user >> Edge(xlabel="HTTPS/HTTP", color="#4caf50") >> igw
    igw >> Edge(color="#4caf50") >> nat
    nat - Edge(style="dotted", color="#999999") - nginx
    nginx >> Edge(xlabel=":8080", color="#2196f3") >> prod
    prod >> Edge(xlabel="outbound", color="#795548") >> nat

    # CI/CD 배포 흐름
    codedeploy >> Edge(xlabel="deploy", color="#ff5722") >> prod