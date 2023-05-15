/**
 *    Copyright 2019 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.moviemanager.adapter.repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import jakarta.validation.Valid;

import org.springframework.stereotype.Repository;

import ch.xxx.moviemanager.domain.model.entity.RevokedToken;
import ch.xxx.moviemanager.domain.model.entity.RevokedTokenRepository;

@Repository
public class RevokedTokenRepositoryBean implements RevokedTokenRepository {
	private JpaRevokedTokenRepository jpaRevokedTokenRepository;

	public RevokedTokenRepositoryBean(JpaRevokedTokenRepository jpaRevokedTokenRepository) {
		this.jpaRevokedTokenRepository = jpaRevokedTokenRepository;
	}

	@Override
	public List<RevokedToken> findAll() {
		return StreamSupport.stream(this.jpaRevokedTokenRepository.findAll().spliterator(), false)
				.collect(Collectors.toList());
	}

	@Override
	public List<RevokedToken> saveAll(@Valid Iterable<RevokedToken> revokedTokens) {
		return StreamSupport.stream(this.jpaRevokedTokenRepository.saveAll(revokedTokens).spliterator(), false)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteAll(Iterable<RevokedToken> revokedTokens) {
		this.jpaRevokedTokenRepository.deleteAll(revokedTokens);
	}
	
	@Override
	public RevokedToken save(@Valid RevokedToken revokedToker) {
		return this.jpaRevokedTokenRepository.save(revokedToker);
	}
}
