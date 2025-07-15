using System;
using System.Collections.Generic;

namespace BusinessObjects.Models;

public partial class WalletTransaction
{
    public int TransactionId { get; set; }

    public int UserId { get; set; }

    public string TransactionType { get; set; } = null!;

    public int Amount { get; set; }

    public string TransactionStatus { get; set; } = null!;

    public DateTime TransactionDate { get; set; }

    public string? OrderCode { get; set; }

    public string? TransactionNote { get; set; }

    public virtual User User { get; set; } = null!;
}
