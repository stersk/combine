package ua.com.tracktor.combine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.tracktor.combine.data.QueryRepository;
import ua.com.tracktor.combine.entity.Query;

@Service
public class QueryService {
    @Autowired
    QueryRepository queryRepository;

    public Query saveQuery(String signature, String account, String body) {
        Query query = new Query();
        query.setSignature(signature);
        query.setAccount(account);
        query.setRequestBody(body);

        return queryRepository.save(query);
    }
}
