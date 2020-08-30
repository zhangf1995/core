package zf.core.rpc.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import zf.core.rpc.TestRpc;

@RestController
public class RpcController {

    @Autowired
    private TestRpc testRpc;

    @RequestMapping(value = "/rpcTest",method = RequestMethod.GET)
    public String test(String id,String id1,String id2){
        return testRpc.getString(id,id1,id2);
    }
}
