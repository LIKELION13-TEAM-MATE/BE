package com.example.team_mate.domain.post.post.service;

import com.example.team_mate.domain.file.file.entity.AttachedFile;
import com.example.team_mate.domain.file.file.repository.AttachedFileRepository;
import com.example.team_mate.domain.member.member.entity.Member;
import com.example.team_mate.domain.member.member.repository.MemberRepository;
import com.example.team_mate.domain.poll.poll.entity.Poll;
import com.example.team_mate.domain.poll.poll.entity.PollOption;
import com.example.team_mate.domain.poll.poll.entity.PollVote;
import com.example.team_mate.domain.poll.poll.repository.PollOptionRepository;
import com.example.team_mate.domain.poll.poll.repository.PollRepository;
import com.example.team_mate.domain.poll.poll.repository.PollVoteRepository;
import com.example.team_mate.domain.post.post.dto.PostCreateRequest;
import com.example.team_mate.domain.post.post.dto.PostResponse;
import com.example.team_mate.domain.post.post.entity.Post;
import com.example.team_mate.domain.post.post.repository.PostRepository;
import com.example.team_mate.domain.project.project.entity.Project;
import com.example.team_mate.domain.project.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final AttachedFileRepository attachedFileRepository;
    private final PollRepository pollRepository;
    private final PollOptionRepository pollOptionRepository;
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";
    private final PollVoteRepository pollVoteRepository;

    /** 게시글 작성 */
    @Transactional
    public PostResponse createPost(
            Long projectId,
            String username,
            PostCreateRequest request,
            List<MultipartFile> files
    ) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트 없음"));
        Member author = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Post post = new Post(request.getTitle(), request.getContent(), author, project);
        postRepository.save(post);

        // 파일 첨부
        if (files != null && !files.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir, "posts", String.valueOf(post.getId())); // /uploads/posts/{postId}/
            Files.createDirectories(uploadPath); // 디렉토리 생성

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName; // 고유 파일명
                    Path filePath = uploadPath.resolve(storedFileName);
                    Files.copy(file.getInputStream(), filePath); // 파일 저장

                    AttachedFile attachedFile = new AttachedFile(
                            originalFileName, storedFileName, filePath.toString(), file.getSize(), post
                    );
                    attachedFileRepository.save(attachedFile);
                }
            }
        }

        // 투표 첨부
        if (request.getPoll() != null) {
            PostCreateRequest.PollCreateDto pollDto = request.getPoll();

            if (pollDto.getTitle() != null && !pollDto.getTitle().trim().isEmpty()) {
                // Poll 생성
                Poll poll = new Poll(pollDto.getTitle(), null, pollDto.isAllowMultiple());
                post.setPoll(poll); // 게시글과 투표 연결 (양방향)
                pollRepository.save(poll);

                if (pollDto.getOptions() != null) {
                    for (PostCreateRequest.PollOptionDto optionDto : pollDto.getOptions()) {
                        if (optionDto.getText() != null && !optionDto.getText().trim().isEmpty()) {
                            PollOption pollOption = new PollOption(optionDto.getText());
                            poll.addOption(pollOption); // 투표와 보기 연결 (양방향)
                            pollOptionRepository.save(pollOption);
                        }
                    }
                }
            }
        }

        return PostResponse.from(post);
    }

    /** 투표하기 */
    @Transactional
    public void vote(List<Long> pollOptionIds, String username) {

        // 선택값
        if (pollOptionIds == null || pollOptionIds.isEmpty()) {
            throw new IllegalArgumentException("투표 항목을 선택해주세요.");
        }

        // 투표자(Member)
        Member voter = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 어떤 투표(Poll)인지
        PollOption firstOption = pollOptionRepository.findById(pollOptionIds.get(0))
                .orElseThrow(() -> new IllegalArgumentException("투표 보기가 없습니다."));
        Poll poll = firstOption.getPoll();

        // 이미 투표했는지 중복 체크
        if (pollVoteRepository.existsByMemberAndPoll(voter, poll)) {
            throw new IllegalArgumentException("이미 참여한 투표입니다.");
        }

        // 득표수 증가
        for (Long optionId : pollOptionIds) {
            PollOption option = pollOptionRepository.findById(optionId)
                    .orElseThrow(() -> new IllegalArgumentException("투표 보기가 없습니다."));

            if (!option.getPoll().getId().equals(poll.getId())) {
                throw new IllegalArgumentException("잘못된 투표 요청입니다.");
            }

            option.addVote();
        }

        // 투표 참여 기록
        PollVote voteRecord = new PollVote(voter, poll);
        pollVoteRepository.save(voteRecord);

    }

    /** 게시글 목록 */
    @Transactional(readOnly = true)
    public List<Post> getPostsByProject(Long projectId) {
        return postRepository.findAllByProjectIdOrderByPinnedDescCreatedDateDesc(projectId);
    }

    /** 게시글 단건 조회 */
    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
    }

    /** 게시글 수정 */
    @Transactional
    public void updatePost(Long postId, String username, String title, String content) {
        Post post = getPostById(postId);

        // 작성자 본인 확인
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        post.update(title, content);
    }

    /** 게시글 삭제 */
    @Transactional
    public void deletePost(Long postId, String username) {
        Post post = getPostById(postId);

        // 작성자 본인 확인
        if (!post.getAuthor().getUsername().equals(username)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    /** 게시글 고정 */
    @Transactional
    public void togglePin(Long postId) {
        Post post = getPostById(postId);

        post.togglePin();
    }
}