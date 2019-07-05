package com.iu.board.qna;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iu.board.BoardDTO;
import com.iu.board.BoardService;
import com.iu.file.FileDAO;
import com.iu.file.FileDTO;
import com.iu.util.FileSaver;
import com.iu.util.PageMaker;

@Service
public class QnaService implements BoardService {

	@Inject//or @Autowired
	private QnaDAO qnaDAO;
	@Inject
	private FileDAO fileDAO;
	@Inject//Inject 안 하고 객체 만들어서 사용해도 상관 없음
	private FileSaver fileSaver;
	
	//reply
	public int setReply(QnaDTO qnaDTO) throws Exception{
		//1. 사전작업
		int result = qnaDAO.setReplyUpdate(qnaDTO);
		//2. insert
		result = qnaDAO.setReply(qnaDTO);
		
		return result;
	}
	
	@Override
	public int setWrite(BoardDTO boardDTO, List<MultipartFile> multipartFiles, HttpSession session) throws Exception {
		//qna Insert
		int result = qnaDAO.setWrite(boardDTO);
		String realPath = session.getServletContext().getRealPath("/resources/qna");
		//files Insert
		//num=boardDTO.getNum()
		//oname = multipartFile.getOriginalName();
		//fname = filesaver.savefile(realpath, multipartFile)
		ArrayList<FileDTO> files = new ArrayList<FileDTO>();
		for(MultipartFile file : multipartFiles) {
			if(file.getOriginalFilename().length() > 0) {
				FileDTO fileDTO = new FileDTO();
				fileDTO.setNum(boardDTO.getNum());
				fileDTO.setOname(file.getOriginalFilename());
				fileDTO.setFname(fileSaver.saveFile(realPath, file));
				files.add(fileDTO);
			}
		}
		result = fileDAO.setWrite(files);
		
		return result;
	}

	@Override
	public int setDelete(int num) throws Exception {
		return qnaDAO.setDelete(num);
	}

	@Override
	public int setUpdate(BoardDTO boardDTO) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BoardDTO getSelect(int num) throws Exception {
		// TODO Auto-generated method stub
		return qnaDAO.getSelect(num);
	}

	@Override
	public List<BoardDTO> getList(PageMaker pageMaker) throws Exception {
		//1. startRow, lastRow
		pageMaker.makeRow();
		List<BoardDTO> list = qnaDAO.getList(pageMaker);
		//2. paging, totalCount
		pageMaker.makePage(qnaDAO.getTotalCount(pageMaker));
		
		return list;
	}
}