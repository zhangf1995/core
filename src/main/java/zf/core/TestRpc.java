package zf.core;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import zf.core.rpc.annotation.CustomRpc;

@CustomRpc(rpcName = "testRpc")
public interface TestRpc {

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    String getString(String id,String ids,String id2);
}
