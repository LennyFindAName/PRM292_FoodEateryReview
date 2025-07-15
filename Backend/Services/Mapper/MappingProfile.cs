namespace Services.Mapper
{
    using AutoMapper;
    using BusinessObjects.Models;
    using BusinessObjects.RequestModels.Authen;
    using BusinessObjects.ResponseModels.Authen;
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;

    /// <summary>
    /// Defines the <see cref="MappingProfile" />
    /// </summary>
    public class MappingProfile : Profile
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="MappingProfile"/> class.
        /// </summary>
        public MappingProfile()
        {
            // Map RegisterRequestModel → User
            CreateMap<RegisterRequestModel, User>()
                .ForMember(dest => dest.Username, opt => opt.MapFrom(src => src.Username))
                .ForMember(dest => dest.Password, opt => opt.MapFrom(src => src.Password)) // sẽ hash ở service
                .ForMember(dest => dest.UserEmail, opt => opt.MapFrom(src => src.Email));

            // Map User → LoginResponseModel
            CreateMap<User, LoginResponseModel>()
                .ForMember(dest => dest.Username, opt => opt.MapFrom(src => src.Username))
                .ForMember(dest => dest.RoleId, opt => opt.MapFrom(src => src.RoleId ?? 0))
                .ForMember(dest => dest.AccessToken, opt => opt.Ignore()); // set trong Service
        }
    }
}
