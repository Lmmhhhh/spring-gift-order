package gift.service;

import gift.auth.JwtProvider;
import gift.domain.Member;
import gift.dto.request.MemberRequest;
import gift.dto.response.MemberResponse;
import gift.exception.DuplicateMemberException;
import gift.exception.MemberNotFoundException;
import gift.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public MemberServiceImpl(MemberRepository memberRepository, JwtProvider jwtProvider){
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public MemberResponse register(MemberRequest request){
        memberRepository.findByEmail(request.email())
                .ifPresent(member -> {
                    throw new DuplicateMemberException();
                });
        Member member = new Member(request.email(),request.pwd());
        Member newMember = memberRepository.save(member);

        String token = jwtProvider.createToken(newMember.getId(), newMember.getEmail());
        return new MemberResponse(token);
    }

    @Override
    public MemberResponse login(MemberRequest request){
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN, "회원이 존재하지 않습니다."));

        if (!member.getPassword().equals(request.pwd())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비밀번호가 일치하지 않습니다.");
        }
        String token = jwtProvider.createToken(member.getId(), member.getEmail());
        return new MemberResponse(token);
    }

    @Override
    public Member findById(Long id){
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException());
    }
}
