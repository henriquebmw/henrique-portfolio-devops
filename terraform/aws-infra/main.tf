provider "aws" {
  region = "eu-west-1"
}
resource "aws_s3_bucket" "demo" {
  bucket = "henrique-demo-bucket"
}