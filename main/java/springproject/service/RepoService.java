package springproject.service;

import com.example.springproject.domain.Repo;

import java.io.IOException;

public interface RepoService {

    public Repo findInfo();

    public void getValue(String url) throws IOException;
}
