# do validation only after authorization

mwe demonstrating how to disable web-binder to do validation only after authorization in springboot 3

## repo content

in master there is state demonstrating the problem of (subjectively) incorrect order of operations, which leaks information about existing endpoint to unauthorized users. 

in solution branch there is one potential fix.

## how to run
a) start app and run individual scripts in directory [bash-scripts](bash-scripts)

b) run test [IsWebBinderUsedToBindJacksonDtoRequestBodyApplicationTests.java](src%2Ftest%2Fjava%2Fcom%2Fexample%2Fis_web_binder_used_to_bind_jackson_dto_request_body%2FIsWebBinderUsedToBindJacksonDtoRequestBodyApplicationTests.java)