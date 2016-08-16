package sme.perf.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sme.perf.request.entity.Attachment;
import sme.perf.requst.dao.AttachmentDao;

@RestController
@RequestMapping("/TestRequestAttachment")
public class TestRequestAttachmentController {
	
	@RequestMapping("/List/{requestId}")
	public @ResponseBody List<Attachment> getByTestRequestId(@PathVariable long requestId){
		AttachmentDao dao = new AttachmentDao();
		return dao.getByTestRequestId(requestId);
	}
}
