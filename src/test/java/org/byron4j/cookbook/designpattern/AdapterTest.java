package org.byron4j.cookbook.designpattern;

import org.byron4j.cookbook.designpattern.adapter.AccountAdapter;
import org.byron4j.cookbook.designpattern.adapter.OffshoreAccount;
import org.byron4j.cookbook.designpattern.adapter.StandardAccount;
import org.junit.Test;

public class AdapterTest {
    @Test
    public void test(){
        StandardAccount sa = new StandardAccount(2000);
        System.out.println("Account Balance= " + sa.getBalance());

        //Calling getBalance() on Adapter
        AccountAdapter adapter = new AccountAdapter(new OffshoreAccount(2000));
        System.out.println("Account Balance= " + adapter.getBalance());
    }
}
