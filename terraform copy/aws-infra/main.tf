
terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = { source = "hashicorp/aws", version = ">= 5.0" }
  }
}
provider "aws" { region = "eu-west-1" }
module "web" { source = "../modules/webapp" app_name = "xp-portfolio" port = 80 }
