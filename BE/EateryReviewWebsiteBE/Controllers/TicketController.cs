using BusinessObjects.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace EateryReviewWebsiteBE.Controllers
{
    [Route("api/ticket")]
    [ApiController]
    public class TicketController : ControllerBase
    {
        private readonly EateryReviewDbContext _context;

        public TicketController(EateryReviewDbContext context)
        {
            _context = context;
        }

        // GET: api/ticket
        [HttpGet]
        public IActionResult GetTickets([FromQuery] int userId)
        {

            var tickets = _context.Tickets
                .Include(t => t.Type)
                .Where(t => t.CreatorId == userId)
                .OrderByDescending(t => t.TicketTime)
                .Select(t => new
                {
                    t.TicketId,
                    t.TicketContent,
                    t.TicketTime,
                    t.TicketStatus,
                    TypeContent = t.Type.TicketTypeContent
                })
                .ToList();

            return Ok(tickets);
        }


        // GET: api/ticket/types
        [HttpGet("types")]
        public IActionResult GetTicketTypes()
        {
            var types = _context.TicketTypes
                .Select(t => new
                {
                    t.TypeId,
                    t.TicketTypeContent
                })
                .ToList();

            return Ok(types);
        }

        // POST: api/ticket
        [HttpPost]
        public IActionResult PostTicket([FromBody] TicketCreateRequest request)
        {
            if (request.CreatorId == null || request.CreatorId <= 0)
            {
                return BadRequest("Missing or invalid user ID.");
            }

            var ticket = new Ticket
            {
                TypeId = request.TypeId,
                TicketContent = request.TicketContent,
                CreatorId = request.CreatorId.Value, // ✅ lấy từ request
                TicketTime = DateTime.Now,
                TicketStatus = 0
            };

            _context.Tickets.Add(ticket);
            _context.SaveChanges();

            return Ok(new { message = "Ticket created", ticket.TicketId });
        }


    }

    public class TicketCreateRequest
    {
        public int? TypeId { get; set; }
        public string? TicketContent { get; set; }
        public int? CreatorId { get; set; }
    }
}
