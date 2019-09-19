API service for api.fpki.io
===========================

- [Introduction](#introduction) 
- [Quick Start](#quick-start)
- [Documentation](#documentation)
- [License](#license)


Introduction
------------



Quick Start
-----------

```
mvn clean package

aws cloudformation describe-stacks

aws cloudformation delete-stack --stack-name api-fpki-io

aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket api.fpki.io

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



