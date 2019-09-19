API service for api.fpki.io
===========================

- [Introduction](#introduction) 
- [Quick Start](#quick-start)
- [Documentation](#documentation)
- [License](#license)


Introduction
------------

The intent of this repo is to maintain a fairly simple rest API using AWS Lambda and AWS DynamoDB.

The rest service maintains a list of CA certificates that are part of the [U.S. Federal PKI](https://fpki.idmanagement.gov/).

The API will only allow CA certificates with *unique* public keys to be added.  This ensures that a given CA certificate will only appear once, in only one certificate path.  This is to avoid confusing relying party applications on *which* path to use when validating a certificate.  For a given CA certificate to be added, the issuing CA *must* have already been submitted to the API.  Below is the JSON object for the Root CA, and all entries that are submitted *must* have a valid path to this Root.

```
{
	"caAKI": "TRUST_ANCHOR_NOT_APPLICABLE",
	"caCert": "MIIEYDCCA0igAwIBAgICATAwDQYJKoZIhvcNAQELBQAwWTELMAkGA1UEBhMCVVMxGDAWBgNVBAoTD1UuUy4gR292ZXJubWVudDENMAsGA1UECxMERlBLSTEhMB8GA1UEAxMYRmVkZXJhbCBDb21tb24gUG9saWN5IENBMB4XDTEwMTIwMTE2NDUyN1oXDTMwMTIwMTE2NDUyN1owWTELMAkGA1UEBhMCVVMxGDAWBgNVBAoTD1UuUy4gR292ZXJubWVudDENMAsGA1UECxMERlBLSTEhMB8GA1UEAxMYRmVkZXJhbCBDb21tb24gUG9saWN5IENBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2HX7NRY0WkG/Wq9cMAQUHK14RLXqJup1YcfNNnn4fNi9KVFmWSHjeavUeL6wLbCh1bI1FiPQzB6+Duir3MPJ1hLXp3JoGDG4FyKyPn66CG3G/dFYLGmgA/Aqo/Y/ISU937cyxY4nsyOl4FKzXZbpsLjFxZ+7xaBugkC7xScFNknWJidpDDSPzyd6KgqjQV+NHQOGgxXgVcHFmCye7Bpy3EjBPvmE0oSCwRvDdDa3ucc2Mnr4MrbQNq4iGDGMUHMhnv6DOzCIJOPpwX7e7ZjHH5IQip9bYi+dpLzVhW86/clTpyBLqtsgqyFOHQ1O5piF5asRR12dP8QjwOMUBm7+nQIDAQABo4IBMDCCASwwDwYDVR0TAQH/BAUwAwEB/zCB6QYIKwYBBQUHAQsEgdwwgdkwPwYIKwYBBQUHMAWGM2h0dHA6Ly9odHRwLmZwa2kuZ292L2ZjcGNhL2NhQ2VydHNJc3N1ZWRCeWZjcGNhLnA3YzCBlQYIKwYBBQUHMAWGgYhsZGFwOi8vbGRhcC5mcGtpLmdvdi9jbj1GZWRlcmFsJTIwQ29tbW9uJTIwUG9saWN5JTIwQ0Esb3U9RlBLSSxvPVUuUy4lMjBHb3Zlcm5tZW50LGM9VVM/Y0FDZXJ0aWZpY2F0ZTtiaW5hcnksY3Jvc3NDZXJ0aWZpY2F0ZVBhaXI7YmluYXJ5MA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUrQx6dVzl85jEeZgOrCj9l/TnAvwwDQYJKoZIhvcNAQELBQADggEBAI9z2uF/gLGH9uwsz9GEYx728Yi3mvIRte9UrYpuGDco71wb5O9Qt2wmGCMiTR0mRyDpCZzicGJxqxHPkYnos/UqoEfAFMtOQsHdDA4b8Idb7OV316rgVNdF9IU+7LQd3nyKf1tNnJaK0KIyn9psMQz4pO9+c+iR3Ah6cFqgr2KBWfgAdKLI3VTKQVZHvenAT+0g3eOlCd+uKML80cgX2BLHb94u6b2akfI8WpQukSKAiaGMWMyDeiYZdQKlDn0KJnNR6obLB6jI/WNaNZvSr79PMUjBhHDbNXuaGQ/lj/RqDG8z2esccKIN47lQA2EC/0rskqTcLe4qNJMHtyznGI8=",
	"caCrl": "http://http.fpki.gov/fcpca/fcpca.crl",
	"caHash": "894EBC0B23DA2A50C0186B7F8F25EF1F6B2935AF32A94584EF80AAF877A3A06E",
	"caIssuer": "CN=Federal Common Policy CA,OU=FPKI,O=U.S. Government,C=US",
	"caNotAfter": "2030-12-01T16:45:27.000+0000",
	"caNotBefore": "2010-12-01T16:45:27.000+0000",
	"caSerial": "0130",
	"caSKI": "AD0C7A755CE5F398C479980EAC28FD97F4E702FC",
	"caSubject": "CN=Federal Common Policy CA,OU=FPKI,O=U.S. Government,C=US"
}
```

Quick Start
-----------

In order to launch this serverless stack, you will need to install and configure the AWS CLI for your platform.

Afterwards, you will need to create an S3 bucket to package and deploy the stack.

```
mvn clean package

aws cloudformation describe-stacks

aws cloudformation delete-stack --stack-name api-fpki-io

aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket {S3-BUCKET-NAME}

aws cloudformation deploy --template-file output-sam.yaml --stack-name api-fpki-io --capabilities CAPABILITY_IAM

aws cloudformation describe-stacks
```

See http://docs.aws.amazon.com/lambda/latest/dg/deploying-lambda-apps.html.


Documentation
-------------



License
-------

This project is in the worldwide [public domain](LICENSE.md). 

> This project is in the public domain within the United States, and copyright and related rights in the work worldwide are waived through the [CC0 1.0 Universal public domain dedication](https://creativecommons.org/publicdomain/zero/1.0/).
>
> All contributions to this project will be released under the CC0 dedication. By submitting a pull request, you are agreeing to comply with this waiver of copyright interest.



