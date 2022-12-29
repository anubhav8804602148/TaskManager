package task.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import task.models.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{

	@Query(value="select * from Task t LIMIT 100", nativeQuery=true)
	public List<Task> findFirstHundredTasks();
	
	@Query(value="select * from Task t where t.status <> 'COMPLETED' LIMIT 100", nativeQuery=true)
	public List<Task> findUncompletedHundredTasks();

	@Query(value="select * from Task t where t.status = 'COMPLETED' LIMIT ?1", nativeQuery=true)
	public List<Task> findFirstNCompletedTasks(int n);
		
}
